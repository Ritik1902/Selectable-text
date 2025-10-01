require "json"

package = JSON.parse(File.read(File.join(__dir__, "../package.json")))

Pod::Spec.new do |s|
  s.name         = "RNSelectableText"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = package["description"]
  s.homepage     = "https://github.com/Ritik1902/Selectable-text"
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/Ritik1902/Selectable-text.git", :tag => "#{s.version}" }

  s.source_files = "*.{h,m,mm}"

  s.dependency "React-Core"
end
