
timestamps
{
	//noinspection GroovyAssignabilityCheck
	node
	{
		try
		{
			abortable
			{
				stage 'Checkout'
				checkout scm

				stage 'Config'
				sh "java -version"

				env.JAVA_HOME = "${tool 'jdk 1.8.0_60'}"
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
				def antHome = tool 'Ant version 1.8.2'

				sh "java -version"
				sh "${antHome}/bin/ant -version"

				stage 'Build'
				sh "${antHome}/bin/ant clean jenkins \"-Dbuild.revision=TODO build.revision\" \"-Dbuild.tag=TODO build.tag\" -Dfindbugs.output=xml"

				stage 'Publish'
				step([$class: 'FindBugsPublisher', pattern: 'build/findbugs.xml', unstableTotalAll: '0'])
				step([$class: 'WarningsPublisher', canComputeNew: false, canResolveRelativePaths: false, consoleParsers: [[parserName: 'Java Compiler (javac)']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: ''])
				archive 'build/success/*'

				if (!'SUCCESS'.equals(currentBuild.result))
				{
					echo("Delete working dir after SUCCESS");
					deleteDir()
				}
			}
		}
		catch (Exception e)
		{
			//todo handle script returned exit code 143
			throw e;
		}
		finally
		{
			// because junit failure aborts ant
			step([$class: 'JUnitResultArchiver', testResults: 'build/testresults/*.xml'])

			def to = emailextrecipients([
					[$class: 'CulpritsRecipientProvider'],
					[$class: 'RequesterRecipientProvider']
			])
			//TODO details
			step([$class            : 'Mailer',
					recipientProviders: to,
					attachLog         : true])
		}
	}
}

def abortable(Closure body)
{
	try
	{
		body.call();
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e;
	}
}
