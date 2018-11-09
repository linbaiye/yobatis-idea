package org.nalby.yobatis.idea.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class JavaToXmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    private final static String NAV_TIP = "Navigate to xml mapper definition.";

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            Collection<? super RelatedItemLineMarkerInfo> result) {
        AbstractPsiElementAnchorSeeker seeker = PsiElementAnchorSeekerFactory.getInstance().create(element);
        if (seeker == null) {
            return;
        }
        XmlTag tag = (XmlTag) seeker.seek();
        if (tag != null) {
            NavigationGutterIconBuilder<PsiElement> identifierToXml =
                NavigationGutterIconBuilder.create(Icons.MYBATIS_ICON).
                    setTargets(tag.getAttribute("id") == null ? tag : tag.getAttribute("id")).
                    setTooltipText(NAV_TIP);
            result.add(identifierToXml.createLineMarkerInfo(element));
        }
    }
}
