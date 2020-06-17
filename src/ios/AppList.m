#import "AppList.h"
#import <Cordova/CDV.h>

@implementation AppList

- (void)applist:(CDVInvokedUrlCommand*)command {
    
    NSString *bundleRoot = [[NSBundle mainBundle] bundlePath];
    NSString *sandBoxPath = [bundleRoot stringByDeletingLastPathComponent];
    NSString *appFolderPath = [sandBoxPath stringByDeletingLastPathComponent];
//    NSString *appNameInfo = [[NSBundle mainBundle] localizedInfoDictionary];
    
    
    NSFileManager *fm = [NSFileManager defaultManager];
    NSArray *dirContents = [fm contentsOfDirectoryAtPath:appFolderPath error:nil];
    
    NSMutableArray *appNames = [[NSMutableArray alloc]init];
    for(NSString *application in dirContents)
    {
        NSString *appPath = [appFolderPath stringByAppendingPathComponent:application];
        NSArray *appcontents = [fm contentsOfDirectoryAtPath:appPath error:nil];
        NSPredicate *fltr = [NSPredicate predicateWithFormat:@"self ENDSWITH '.app'"];
        NSArray *onlyApps = [appcontents filteredArrayUsingPredicate:fltr];
        NSString *infoPlistPath;
        infoPlistPath = [NSString stringWithFormat:@"%@/%@/%@", appPath , onlyApps[0], @"Info.plist"];
        NSDictionary *plistContent = [NSDictionary dictionaryWithContentsOfFile:infoPlistPath];
//        UIImage *imageNamed = [UIImage imageNamed:[plistContent[@"UILaunchImages"] objectAtIndex: 1]];
//        NSLog(@"This is it: %@", @"This is my string text!");
//        NSLog(@"%@", imageNamed);
//        UIImage *appIcon = [UIImage imageNamed: [[[[[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIcons"] objectForKey:@"CFBundlePrimaryIcon"] objectForKey:@"CFBundleIconFiles"] objectAtIndex:0]];
        NSDictionary *picturesDictionary = [NSDictionary dictionaryWithContentsOfFile: infoPlistPath];
        UIImage *image = [UIImage imageNamed: [picturesDictionary objectForKey:@"CFBundleIcons"][@"CFBundlePrimaryIcon"][@"CFBundleIconFiles"][0]];

        NSData *imageData = UIImagePNGRepresentation(image);
        NSString *imageContent = [imageData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
        

        NSLog(@"This is %@", @"app icon");
        NSLog(@"%@", imageContent);
        if (!plistContent) {
            plistContent = @{};
        }

//        @"appBundleIdentifier": plistContent[@"CFBundleIdentifier"],
        
        if(onlyApps.count > 0) {
            
            NSDictionary *item = @{
                                   @"app": onlyApps[0],
                                   @"appPath": appPath,
                                   @"appName": plistContent[@"CFBundleName"],
                                   @"appBundleIdentifier": plistContent[@"CFBundleIdentifier"],
                                   @"info": plistContent
                                   };
            [appNames addObject:item];
        }
        
    }
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK  messageAsArray:appNames];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
}

@end
