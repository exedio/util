
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
				sh 'git rev-parse HEAD > GIT_COMMIT'
				env.GIT_COMMIT = readFile('GIT_COMMIT').trim()
				sh "git cat-file -p HEAD | grep '^tree ' | sed -e 's/^tree //' > GIT_TREE"
				env.GIT_TREE = readFile('GIT_TREE').trim()

				stage 'Config'
				env.BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd_HH-mm-ss");
				env.JAVA_HOME = "${tool 'jdk 1.8.0_60'}"
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
				def antHome = tool 'Ant version 1.8.2'

				sh "java -version"
				sh "${antHome}/bin/ant -version"
				sh 'echo' +
						' GIT_COMMIT -${GIT_COMMIT}-' +
						' GIT_TREE -${GIT_TREE}-' +
						' BUILD_TIMESTAMP -${BUILD_TIMESTAMP}-' +
						' BRANCH_NAME -${BRANCH_NAME}-' +
						' CHANGE_ID -${CHANGE_ID}-' +
						' CHANGE_URL -${CHANGE_URL}-' +
						' CHANGE_TARGET -${CHANGE_TARGET}-' +
						' BUILD_NUMBER -${BUILD_NUMBER}-' +
						' BUILD_ID -${BUILD_ID}-'

				stage 'Build'
				sh "${antHome}/bin/ant clean jenkins " +
						'"-Dbuild.revision=${BUILD_NUMBER}" ' +
						'"-Dbuild.tag=git ${BRANCH_NAME} ${GIT_COMMIT} ${GIT_TREE} jenkins ${BUILD_NUMBER} ${BUILD_TIMESTAMP}" ' +
						'-Dfindbugs.output=xml'

				stage 'Publish'
				step([$class: 'WarningsPublisher',
						unstableTotalAll: '0',
						canComputeNew: false,
						canResolveRelativePaths: true,
						consoleParsers: [[parserName: 'Java Compiler (javac)']]])
				step([$class: 'FindBugsPublisher',
						unstableTotalAll: '0',
						pattern: 'build/findbugs.xml'])
				archive 'build/success/*'
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

			if ('SUCCESS'.equals(currentBuild.result))
			{
				echo("Delete working dir after SUCCESS");
				deleteDir()
			}
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
