package org.nalby.yobatis.idea.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class XmlToJavaLineMarkerProvider extends RelatedItemLineMarkerProvider {

    private final static String NAV_TIP = "Navigate to java method declaration.";

    private void collectNavMarkerForAttr(@NotNull XmlAttribute xmlAttribute, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!"id".equals(xmlAttribute.getName()) || xmlAttribute.getValue() == null) {
            return;
        }
        String id = xmlAttribute.getValue();
        if (id != null) {
//        if (MapperReservedIdentifierx.contains(id)) {
            return;
        }
        PsiElement element = xmlAttribute;
        while (element != null && !(element instanceof XmlFile)) {
            element = element.getParent();
        }
        if (element == null) {
            return;
        }
        PsiClass interfaze = YobatisPsiUtil.findCorrespondingJavaInterface((XmlFile) element);
        if (interfaze == null) {
            return;
        }
        for (PsiMethod method: interfaze.getMethods()) {
            if (!xmlAttribute.getValue().equals(method.getName())) {
                continue;
            }
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(Icons.MYBATIS_ICON).
                            setTarget(method.getNameIdentifier()).
                            setTooltipTitle(NAV_TIP);
            result.add(builder.createLineMarkerInfo(xmlAttribute));
            break;
        }
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof XmlAttribute) {
            collectNavMarkerForAttr((XmlAttribute)element, result);
        }
    }
}
