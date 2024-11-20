import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
@Suite
@SelectPackages("by.bsu.dependency")
@IncludePackages({
        "by.bsu.dependency.context",
        "by.bsu.dependency.example",
        "by.bsu.dependency.exceptions"
})
class TestAll {
}
