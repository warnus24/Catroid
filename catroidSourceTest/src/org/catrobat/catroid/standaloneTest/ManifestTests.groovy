package org.catrobat.catroid.standaloneTest
import com.sun.deploy.xml.XMLParser
import org.junit.Before
import org.junit.Test

public class ManifestTests {
    def manifestXML

    @Before void setUp() {
        manifestXML = new XMLParser().parse(new File("src/main/AndroidManifest.xml"))
    }

    @Test
    public void testAppName() {
        def codeXML = new XMLParser().parse(new File("src/main/assets/standalone/code.xml"))
        println manifestXML.application.attribute("android:label")
        assert manifestXML.application.attribute("android:label") == codeXML.header.programName
    }

    public void testVersionName() {
        assert manifestXML.attribute("android:VersionName") == "1.0"
    }

    public void testVersionCode() {
        assert manifestXML.attribute("android:VersionCode") == "1"
    }

    public void testPackageName() {
        def codeXML = new XMLParser().parse(new File("src/main/assets/standalone/code.xml"))
        def programName = codeXML.header.programName
        programName = programName.replaceAll(" ", "")
        assert manifestXML.attribute("package") == ("org.catrobat.catroid." + programName) //change this!
    }
}
