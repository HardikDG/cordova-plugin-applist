var AppList = {
	applist : function (success, error) {
		cordova.exec((success, error, 'AppList', 'applist', []));
	},
	appicon: function (pkg, success, error) {
		cordova.exec((success, error, 'AppList', 'appicon', [pkg]));
	},
	appstart: function (pkg, success, error) {
		cordova.exec((success, error, 'AppList', 'appstart', [pkg]));
	}
};
module.exports = AppList;
