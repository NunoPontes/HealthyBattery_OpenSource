# HealthyBattery
An Android application to help the user have good habits, so that the battery can last longer.


##Why I made this:
-It was on my to-do list for a while, so why not ;)

-Use battery manager class

-To learn more things about Android

-To have the chance to personalize my app

#Features:
-Show basic data about the battery such as: 

                                            -Status
                                            
                                            -Type of plug
                                            
                                            -Battery Level
                                            
                                            -Health
                                            
                                            -Battery Technology
                                            
                                            -Battery Temperature
                                            
                                            -Battery Voltage
                                            
-Graph with battery evolution

-Notification (Alert Dialog (for now)) (when the app is open) if the battery is too high or too low and if the temperature is too high or to low

-Settings to define: 

                     -Temperature format (ºC or F)
                     
                     -Notification with one play/loop sound and with or withou vibration
                     
                     -Choose the battery level of when you want to be notified (recommended: 30%)
                     
-Tips to make the battery last longer and stay in good health

-Service that collects the battery level (in normal mode and in DOZE, every 30 minutes) (Still in Beta and it's buggy)

##Permissions:
    Vibrate: <uses-permission android:name="android.permission.VIBRATE"/>
    Save battery data: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    Save battery data: <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    For crash analytics(temporary): <uses-permission android:name="android.permission.INTERNET"/>
    
    
##Dependencies
To Bind Android views and callbacks to fields and methods: https://github.com/JakeWharton/butterknife

To make the graph: https://github.com/appsthatmatter/GraphView

<html>
<a href='https://play.google.com/store/apps/details?id=com.keepitsimplestudios.healthybattery&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a></html>


