module.exports = { 
  "preset": "jest-puppeteer", 
  "testRegex": "(/__tests__/Test.*|(\\.|/)(test|spec))\\.[jt]sx?$",
  "testPathIgnorePatterns" : ["__tests__/TestDefaultProjects.js","__tests__/TestGeppettoModel.js","__tests__/TestLiveGeppetto.js",
	  						  "__tests__/TestUIComponents.js"]
};