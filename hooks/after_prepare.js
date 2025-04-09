const fs = require('fs');
const path = require('path');

module.exports = function (context) {
  const platformRoot = path.join(context.opts.projectRoot, 'platforms', 'android');
  const settingsGradle = path.join(platformRoot, 'settings.gradle');
  const appGradle = path.join(platformRoot, 'app', 'build.gradle');

  const smartwatchInclude = "\ninclude ':smartwatch'\nproject(':smartwatch').projectDir = new File(rootProject.projectDir, 'smartwatch')\n";
  const smartwatchDependency = "implementation project(':smartwatch')";

  // Atualizar settings.gradle
  if (fs.existsSync(settingsGradle)) {
    let content = fs.readFileSync(settingsGradle, 'utf-8');
    if (!content.includes("include ':smartwatch'")) {
      fs.appendFileSync(settingsGradle, smartwatchInclude);
      console.log('✅ settings.gradle atualizado com módulo :smartwatch');
    } else {
      console.log('ℹ️ settings.gradle já contém o módulo :smartwatch');
    }
  } else {
    console.warn('⚠️ settings.gradle não encontrado');
  }

  // Atualizar app/build.gradle
  /**if (fs.existsSync(appGradle)) {
    let content = fs.readFileSync(appGradle, 'utf-8');
    if (!content.includes(smartwatchDependency)) {
      content = content.replace(/dependencies\s*\{/, match => `${match}\n    ${smartwatchDependency}`);
      fs.writeFileSync(appGradle, content, 'utf-8');
      console.log('✅ app/build.gradle atualizado com dependência :smartwatch');
    } else {
      console.log('ℹ️ app/build.gradle já contém dependência :smartwatch');
    }
  } else {
    console.warn('⚠️ app/build.gradle não encontrado');
  }**/
};