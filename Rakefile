task :publish do
  sh "mvn versions:set -DnewVersion=1.2.0-beta-11"
  sh "mvn clean deploy -P release"
end

# 变更版本
task :change_version, [:ver] do |t, args|
  ver = args[:ver]
  sh "mvn versions:set -DnewVersion=#{ver}"
end