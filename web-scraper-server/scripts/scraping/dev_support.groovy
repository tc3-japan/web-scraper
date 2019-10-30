// Scraping Environment Variables
class Envs {
    // TODO
}

// Developer helper method
class DevHelp {
    static void printHelp() {
        println("""\
                |=================================================================
                |Developer Help Message --- Help ---
                |
                |[Dev Scripts to load] (> :load <script>)
                |  * scripts/scraping/dev_support.groovy (default)
                |  * scripts/scraping/amazon-product-detail__prop-only.groovy
                |
                |[Methods provided by default scripts]
                |  * pwd()                  : todo doc
                |  * ls(options)            : todo doc
                |  * logDir(relativePath)   : todo doc
                |  * devHelp()              : todo doc
                |  * devUsage()             : todo doc
                |
                |[Dev Classes to import] (> import <FQDN>)
                |  * com.topcoder.common.util.dev.DevSupport
                |
                |=================================================================
                |""".stripMargin()
        )
    }
    static void printUsage() {
        println("""\
                |=================================================================
                |Developer Help Message --- Usage ---
                |
                |[usage]
                |  > import com.topcoder.common.util.dev.*
                |  > client = DevSupport.getWebClient()
                |  > pwd()
                |  > ls "-l logs/amazon"
                |  product-yyyy-mm-ddTHH-MI-SS.SSS.html
                |  ...
                |  > page = client.getPage(logDir("logs/amazon/product-yyyy-mm-ddTHH-MI-SS.SSS.html"))
                |  > :l "scripts/scraping/amazon-product-detail__prop-only.groovy"
                |  > price = page.querySelector(Vars.prices[0]).asText()
                |
                |=================================================================
                |""".stripMargin()
        )
    }
}

// OS Command Support class and command method
class OsCommandSupport {
    static def execute_command_with_options(command, options) {
        if (options != null) {
            (command + " " + options).execute().text.trim()
        } else {
            command.execute().text.trim()
        }
    }
}

def devHelp() {
    DevHelp.printHelp()
}
def devUsage() {
    DevHelp.printUsage()
}
def logDir(relativePath) {
    "file://" + OsCommandSupport.execute_command_with_options("pwd", null) + "/" + relativePath
}
def pwd() {
    OsCommandSupport.execute_command_with_options("pwd", null)
}
def ls(options) {
    OsCommandSupport.execute_command_with_options("ls", options)
}

DevHelp.printHelp()
