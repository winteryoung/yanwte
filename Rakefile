task :publish do
  sh "mvn versions:set -DnewVersion=1.2.0-beta-11"
  sh "mvn clean deploy -P release"
end
