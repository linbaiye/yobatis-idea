package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.HashSet;
import java.util.Set;

class PsiElementAnchorSeekerFactory {
    private static final PsiElementAnchorSeekerFactory instance = new PsiElementAnchorSeekerFactory();
    private PsiElementAnchorSeekerFactory() { }

    static PsiElementAnchorSeekerFactory getInstance() {
        return instance;
    }

    private final static Set<String> LEGACY_NAMES;

    private final static Set<String> TAG_NAMES;

    static {
        LEGACY_NAMES = new HashSet<>();
        LEGACY_NAMES.add("insertAll");
        LEGACY_NAMES.add("updateAll");
        LEGACY_NAMES.add("updateAllByCriteria");

        TAG_NAMES = new HashSet<>();
        TAG_NAMES.add("insert");
        TAG_NAMES.add("select");
        TAG_NAMES.add("delete");
        TAG_NAMES.add("update");
    }

    private boolean isLegacyMapper(XmlFile xmlFile) {
        if (xmlFile.getRootTag() == null) {
            return false;
        }
        int counter = 0;
        for (XmlTag tag : xmlFile.getRootTag().getSubTags()) {
            if (LEGACY_NAMES.contains(tag.getAttributeValue("id"))) {
                counter += 1;
            }
        }
        return counter == LEGACY_NAMES.size();
    }

    private  PsiClass getDaoClass(PsiJavaFile javaFile) {
        if (javaFile == null ||
                !javaFile.getName().endsWith("Dao.java") ||
                javaFile.getClasses().length != 1) {
            return null;
        }
        PsiClass thisClass = javaFile.getClasses()[0];
        if (!thisClass.isInterface()) {
            return null;
        }
        return thisClass;
    }

    private boolean isNoBaseDao(PsiJavaFile javaFile) {
        PsiClass psiClass = getDaoClass(javaFile);
        return psiClass != null && psiClass.getSupers().length == 1;
    }

    private boolean isBaseAwareDao(PsiJavaFile javaFile) {
        PsiClass thisClass = getDaoClass(javaFile);
        if (thisClass == null) {
            return false;
        }
        PsiClass[] psiClasses = thisClass.getSupers();
        if (psiClasses.length != 2) {
            return false;
        }
        for (PsiClass psiClass: psiClasses) {
            if (psiClass.isInterface() && "BaseDao".equals(psiClass.getName())) {
                return true;
            }
        }
        return false;
    }


    private <R> R travelToRoot(PsiElement node, Class<R> rootClass) {
        PsiElement element = node;
        while (element != null && element != element.getParent() && !rootClass.isAssignableFrom(element.getClass())) {
            element = element.getParent();
        }
        return element == null ? null : rootClass.cast(element);
    }

    AbstractPsiElementAnchorSeeker create(PsiElement psiElement) {
        if ((psiElement instanceof PsiIdentifier) && (psiElement.getParent() instanceof PsiClass)) {
            PsiJavaFile javaFile = travelToRoot(psiElement, PsiJavaFile.class);
            if (javaFile != null && javaFile.getName().endsWith("Dao.java")) {
                return new JavaInterfaceAnchorSeeker(javaFile);
            }
        } else if ((psiElement instanceof PsiIdentifier) && (psiElement.getParent() instanceof PsiMethod)) {
            PsiJavaFile javaFile = travelToRoot(psiElement, PsiJavaFile.class);
            if (isNoBaseDao(javaFile)) {
                return new NonbaseDaoMethodAnchorSeeker((PsiIdentifier)psiElement, javaFile);
            } else if (isBaseAwareDao(javaFile)) {
                return new BaseAwarePsiElementAnchorSeeker((PsiIdentifier)psiElement, javaFile);
            }
        } else if (psiElement instanceof XmlAttribute && psiElement.getParent() instanceof XmlTag) {
            if (!TAG_NAMES.contains(((XmlTag) psiElement.getParent()).getName())) {
                return null;
            }
            XmlFile xmlFile = travelToRoot(psiElement, XmlFile.class);
            if (xmlFile == null || !(xmlFile.getName().endsWith("Mapper.xml"))) {
                return null;
            }
            if (!isLegacyMapper(xmlFile)) {
                return new NonbaseXmlTagAnchorSeeker((XmlAttribute) psiElement, xmlFile);
            } else {
                return new BaseAwareXmlMapperAnchorSeeker((XmlAttribute) psiElement, xmlFile);
            }
        }
        return null;
    }

}
