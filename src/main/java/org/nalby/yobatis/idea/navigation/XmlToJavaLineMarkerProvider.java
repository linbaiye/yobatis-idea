package org.nalby.yobatis.idea.navigation;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class XmlToJavaLineMarkerProvider extends RelatedItemLineMarkerProvider {

    private final static String NAV_TIP = "Navigate to java method declaration.";

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        AbstractPsiElementAnchorSeeker seeker = PsiElementAnchorSeekerFactory.getInstance().create(element);
        if (seeker == null) {
            return;
        }
        PsiIdentifier identifier = (PsiIdentifier) seeker.seek();
        if (identifier != null) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(Icons.MYBATIS_ICON).
                            setTarget(identifier).
                            setTooltipTitle(NAV_TIP);
            result.add(builder.createLineMarkerInfo(element));
        }
    }
}
