package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.Set;

public class NonbaseDaoMethodAnchorSeeker extends AbstractPsiElementAnchorSeeker<XmlTag> {

    private PsiIdentifier psiIdentifier;

    private PsiJavaFile psiJavaFile;

    NonbaseDaoMethodAnchorSeeker(PsiIdentifier psiIdentifier,
                                 PsiJavaFile psiJavaFile) {
        this.psiIdentifier = psiIdentifier;
        this.psiJavaFile = psiJavaFile;
    }


    private String findAnchorNameInImpl() {
        Set<PsiJavaFile> files = findJavaFiles(psiJavaFile.getProject(), toNameOfDaoImpl(psiJavaFile.getName()));
        if (files.isEmpty()) {
            return null;
        }
        for (PsiJavaFile file : files) {
            for (PsiMethod method : file.getClasses()[0].getMethods()) {
                if (method.getBody() == null) {
                    continue;
                }
                PsiMethod methodFound = null;
                for (HierarchicalMethodSignature methodSignature : method.getHierarchicalMethodSignature().getSuperSignatures()) {
                    if (methodSignature.getMethod().equals(psiIdentifier.getParent())) {
                        // Good, the method implements found.
                        methodFound = method;
                        break;
                    }
                }
                if (methodFound == null || methodFound.getBody() == null) {
                    continue;
                }
                String anchor = findAnchorNameInMethodImpl(methodFound);
                if (anchor != null) {
                    return anchor;
                }
            }
        }
        return null;
    }

    public XmlTag seek() {
        String anchor = findAnchorNameInImpl();
        if (anchor == null) {
            return null;
        }
        Set<XmlFile> files = findXmlFiles(psiJavaFile.getProject(), toNameOfXmlMapper(psiJavaFile.getName()));
        return findTagInFilesByIdAttribute(files, anchor);
    }
}
