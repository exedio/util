
import hudson.plugins.jacoco.model.CoverageGraphLayout
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue
import java.awt.Color

timestamps
{
	def jdk = 'openjdk-8-deb9'
	def isRelease = env.BRANCH_NAME.toString().equals("master")

	properties([
			buildDiscarder(logRotator(
					numToKeepStr         : isRelease ? '1000' : '30',
					artifactNumToKeepStr : isRelease ? '1000' :  '2'
			))
	])

	//noinspection GroovyAssignabilityCheck
	node('GitCloneExedio && ' + jdk)
	{
		try
		{
			abortable
			{
				echo("Delete working dir before build")
				deleteDir()

				def buildTag = makeBuildTag(checkout(scm))

				env.JAVA_HOME = tool type: 'jdk', name: jdk
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

				sh "ant/bin/ant -noinput clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=' + buildTag + '"' +
						' -Dbuild.status=' + (isRelease?'release':'integration') +
						' -Ddisable-ansi-colors=true' +
						' -Dfindbugs.output=xml'

				recordIssues(
						enabledForFailure: true,
						ignoreFailedBuilds: false,
						qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
						tools: [
							java(),
							spotBugs(pattern: 'build/findbugs.xml', useRankAsPriority: true),
						],
				)
				jacoco(
						changeBuildStatus: true,
						minimumBranchCoverage: '30',
						coverageGraphLayout:
								new CoverageGraphLayout()
								.baseStroke(2f)
								.axis().skipZero().crop(50)
								.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.BLUE)
								.axis().skipZero().crop()
								.plot().type(CoverageType.BRANCH).value(CoverageValue.MISSED).color(Color.RED)
								.axis().skipZero().crop()
								.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.ORANGE),
						execPattern: 'build/jacoco.exec',
						classPattern: 'build/classes/src',
						sourcePattern: 'src')
				archiveArtifacts 'build/success/*'
				plot(
						csvFileName: 'plots.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: 'exedio-cope-util.jar',
						numBuilds: '1000',
						style: 'line',
						useDescr: false,
						propertiesSeries: [
							[ file: 'build/exedio-cope-util.jar-plot.properties',     label: 'exedio-cope-util.jar' ],
							[ file: 'build/exedio-cope-util-src.zip-plot.properties', label: 'exedio-cope-util-src.zip' ],
						],
				)
			}
		}
		catch(Exception e)
		{
			//todo handle script returned exit code 143
			throw e
		}
		finally
		{
			// because junit failure aborts ant
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
			)
			def to = emailextrecipients([isRelease ? culprits() : developers(), requestor()])
			//TODO details
			step([$class: 'Mailer',
					recipients: to,
					attachLog: true,
					notifyEveryUnstableBuild: true])

			echo("Delete working dir after build")
			deleteDir()
		}
	}
}

def abortable(Closure body)
{
	try
	{
		body.call()
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e
	}
}

def makeBuildTag(scmResult)
{
	return 'build ' +
			env.BRANCH_NAME + ' ' +
			env.BUILD_NUMBER + ' ' +
			new Date().format("yyyy-MM-dd") + ' ' +
			scmResult.GIT_COMMIT + ' ' +
			sh (script: "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //'", returnStdout: true).trim()
}
