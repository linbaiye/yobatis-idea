package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.Set;

public class BaseAwarePsiElementAnchorSeeker extends AbstractPsiElementAnchorSeeker<XmlTag> {

    private PsiIdentifier psiIdentifier;

    private PsiJavaFile psiJavaFile;

    BaseAwarePsiElementAnchorSeeker(PsiIdentifier psiIdentifier,
                                     PsiJavaFile psiJavaFile) {
        this.psiIdentifier = psiIdentifier;
        this.psiJavaFile = psiJavaFile;
    }

    private String toNameOfXmlMapper() {
        return psiJavaFile.getName().replaceAll("Dao\\.java", "Mapper.xml");
    }

    @Override
    public XmlTag seek() {
        Set<XmlFile> files = findXmlFiles(psiJavaFile.getProject(), toNameOfXmlMapper());
        return findTagInFilesByIdAttribute(files, psiIdentifier.getText());
    }
}
