var exec = require('cordova/exec');

exports.applist = function (success, error) {
	exec(success, error, 'AppList', 'applist', []);
};
