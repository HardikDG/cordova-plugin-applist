var exec = require('cordova/exec');

exports.applist = function (success, error) {
	exec(success, error, 'AppList', 'applist', []);
};

exports.appicon = function (pkg, success, error) {
	exec(success, error, 'AppList', 'appicon', [pkg]);
};

exports.appstart = function (pkg, success, error) {
	exec(success, error, 'AppList', 'appstart', [pkg]);
};
