module.exports = {
  "preset": "jest-puppeteer", 
  "testRegex": "(/tests/jest-puppeteer/persistence/Test.*|(\\.|/)(test|spec))\\.[jt]sx?$",
  "testPathIgnorePatterns" : [
    "geppetto-client"
  ]
};
