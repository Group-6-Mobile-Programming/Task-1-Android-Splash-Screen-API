// Jenkins declarative pipeline: Checkstyle quality gate for the UNEB e-Results app.
//
// - Works on Windows (bat) and Linux/macOS (sh) Jenkins machines.
// - Needs only Java on the PATH. It does NOT need the Android SDK, so the style
//   check cannot fail because of Android/Gradle setup problems.
// - Any Checkstyle violation with severity "error" FAILS the build (it is blocked).
//   Violations with severity "warning" or "info" are printed but do not fail it.

// Runs a command on any OS and returns its exit status instead of failing the step.
def runCmd(String cmd) {
    return isUnix() ? sh(script: cmd, returnStatus: true) : bat(script: cmd, returnStatus: true)
}

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
        timeout(time: 15, unit: 'MINUTES')
    }

    parameters {
        booleanParam(name: 'RUN_ANDROID_BUILD', defaultValue: false,
                     description: 'Also build the debug APK after Checkstyle passes (needs the Android SDK).')
    }

    environment {
        CHECKSTYLE_VERSION = '10.21.0'
        CHECKSTYLE_JAR     = "tools/checkstyle-${CHECKSTYLE_VERSION}-all.jar"
        CHECKSTYLE_URL     = "https://github.com/checkstyle/checkstyle/releases/download/checkstyle-${CHECKSTYLE_VERSION}/checkstyle-${CHECKSTYLE_VERSION}-all.jar"
        CHECKSTYLE_CONFIG  = 'config/checkstyle/checkstyle.xml'
        CONFIG_LOC         = 'config/checkstyle'
        SOURCE_DIR         = 'app/src/main/java'
        REPORT_DIR         = 'build/checkstyle'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Branch: ${env.GIT_BRANCH ?: 'n/a'}  Commit: ${env.GIT_COMMIT ?: 'n/a'}"
            }
        }

        stage('Prepare Checkstyle') {
            steps {
                script {
                    if (runCmd('java -version') != 0) {
                        error 'Java was not found on the PATH of this Jenkins machine. ' +
                              'Install JDK 17+ or set JAVA_HOME / PATH in Manage Jenkins > System.'
                    }

                    def jarOk = fileExists(env.CHECKSTYLE_JAR) &&
                                runCmd("java -jar ${env.CHECKSTYLE_JAR} --version") == 0
                    if (jarOk) {
                        echo "Checkstyle ${env.CHECKSTYLE_VERSION} already downloaded, reusing it."
                    } else {
                        dir('tools') { deleteDir() }   // remove a missing or half-downloaded jar
                        def dl = runCmd("curl -sSL --fail --create-dirs -o ${env.CHECKSTYLE_JAR} ${env.CHECKSTYLE_URL}")
                        if (dl != 0 || runCmd("java -jar ${env.CHECKSTYLE_JAR} --version") != 0) {
                            error 'Could not download Checkstyle. Check the internet connection of the Jenkins machine.'
                        }
                    }
                }
            }
        }

        stage('Checkstyle') {
            steps {
                script {
                    dir(env.REPORT_DIR) { writeFile file: '.keep', text: '' }   // make sure the folder exists

                    def base = "java -Dconfig_loc=${env.CONFIG_LOC} -jar ${env.CHECKSTYLE_JAR} -c ${env.CHECKSTYLE_CONFIG}"

                    // 1) XML report for the archive (its exit status is ignored here).
                    runCmd("${base} -f xml -o ${env.REPORT_DIR}/checkstyle-result.xml ${env.SOURCE_DIR}")

                    // 2) Readable report in the console. This run is the quality gate.
                    def status = runCmd("${base} ${env.SOURCE_DIR}")

                    // Second opinion from the XML: exit codes wrap around at 256 on Linux.
                    def xmlErrors = readFile("${env.REPORT_DIR}/checkstyle-result.xml")
                                        .split('severity="error"', -1).length - 1

                    if (status != 0 || xmlErrors > 0) {
                        error "Checkstyle quality gate FAILED: ${xmlErrors} style error(s). " +
                              'Fix the [ERROR] lines above to unblock the build.'
                    }
                    echo 'Checkstyle quality gate PASSED: no style errors.'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'build/checkstyle/checkstyle-result.xml', allowEmptyArchive: true
                }
            }
        }

        stage('Build debug APK') {
            when { expression { return params.RUN_ANDROID_BUILD } }
            steps {
                script {
                    def status = isUnix()
                        ? sh(script: 'chmod +x gradlew && ./gradlew assembleDebug --no-daemon', returnStatus: true)
                        : bat(script: 'gradlew.bat assembleDebug --no-daemon', returnStatus: true)
                    if (status != 0) {
                        error 'Android build failed. Check ANDROID_HOME (or sdk.dir in local.properties) and JDK 17+.'
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success { echo 'RESULT: SUCCESS. The code follows the style rules.' }
        failure { echo 'RESULT: FAILED. The build was blocked. See the stage output above.' }
    }
}
