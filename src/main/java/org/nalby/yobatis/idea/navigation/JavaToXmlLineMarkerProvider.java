package org.nalby.yobatis.idea.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class JavaToXmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    private final static String NAV_TIP = "Navigate to xml mapper definition.";

    private PsiJavaFile travelToJavaFile(PsiIdentifier psiIdentifier) {
        PsiElement element = psiIdentifier;
        while (element != null && !(element instanceof PsiJavaFile)) {
            element = element.getParent();
        }
        return (PsiJavaFile)element;
    }

    private void addNavIconToMethodIdentifier(PsiIdentifier psiIdentifier, Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiJavaFile psiJavaFile = travelToJavaFile(psiIdentifier);
        if (psiJavaFile == null) {
            return;
        }
        XmlFile xml = YobatisPsiUtil.findCorrespondingMapperFile(psiJavaFile);
        if (xml == null) {
            return;
        }
        for (XmlTag xmlTag : xml.getRootTag().getSubTags()) {
            if (!psiIdentifier.getText().equals(xmlTag.getAttributeValue("id"))) {
                continue;
            }
            NavigationGutterIconBuilder<PsiElement> identifierToXml =
                    NavigationGutterIconBuilder.create(Icons.MYBATIS_ICON).
                            setTargets(xmlTag.getAttribute("id")).
                            setTooltipText(NAV_TIP);
            result.add(identifierToXml.createLineMarkerInfo(psiIdentifier));
            break;
        }
    }

    private void addNavIconToClassIdentifier(@NotNull PsiIdentifier psiIdentifier,
                                             Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiJavaFile psiJavaFile = travelToJavaFile(psiIdentifier);
        if (psiJavaFile == null) {
            return;
        }
        XmlFile xml = YobatisPsiUtil.findCorrespondingMapperFile(psiJavaFile);
        if (xml == null) {
            return;
        }
        NavigationGutterIconBuilder<PsiElement> identifierToXml =
                NavigationGutterIconBuilder.create(Icons.MYBATIS_ICON).
                        setTargets(xml.getRootTag()).
                        setTooltipText(NAV_TIP);
        result.add(identifierToXml.createLineMarkerInfo(psiIdentifier));
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiIdentifier)) {
            return;
        }
        if (element.getParent() instanceof PsiMethod) {
            addNavIconToMethodIdentifier((PsiIdentifier) element, result);
        } else if (element.getParent() instanceof PsiClass) {
            addNavIconToClassIdentifier((PsiIdentifier) element, result);
        }
    }
}
