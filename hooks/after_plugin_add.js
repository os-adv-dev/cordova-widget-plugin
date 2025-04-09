const fs = require('fs');
const path = require('path');

module.exports = function (context) {
    const pluginDir = context.opts.plugin.dir; // Pasta onde o plugin foi extraído
    const smartwatchSrc = path.join(pluginDir, 'src', 'android', 'smartwatch'); // Ajuste conforme seu plugin
    const dest = path.join(context.opts.projectRoot, 'platforms', 'android', 'smartwatch');

    console.log('---- ✅ dest:', dest);

    if (!fs.existsSync(smartwatchSrc)) {
        console.error('🚫 smartWatchSrc não encontrado:', smartwatchSrc);
        return;
    }

    fs.cpSync(smartwatchSrc, dest, { recursive: true });
    console.log('✅ Módulo smartwatch copiado com sucesso!');
};