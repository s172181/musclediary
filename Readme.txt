**Muscle Diary

This app is used to connect to Shimmer Sensor using Shimmer API and load EMG Data.

For this version when the user starts the app a dialogue window appears asking "Do you want to use a Shimmer sensor or skip this to navigate the app?". This is only intended to be used by evaluators of the "02808 Personal Data Interaction for Mobile and Wearables" course. If the user selects "skip", the user can navigate through the app without the necessity of connecting with the Shimmer sensor, and the results displaying on "Result Screens" are obtained from sample data (from an example file). Be aware that it takes a few seconds to display data (depending on device capacity). 

*Remember* to enable bluetooth in order to connect to shimmer

***Things yet to be improved/implemented
1. Sometimes the connection is lost in "ListMuscle" screen. When this happens user has to go back to connection screen and load connect again.
2. When a session is loaded and displayed in result screen if the user wants to load another session, he/she has to start the app again. This has to be fixed in next version.
3. On results screen EMG graph and active training session, the maximum amplitude and mean amplitude are calculated from the load data from EMG SHimmer sensor, however, last session values are fixed/fake. These values are supposed to display the percentage of progress from the previous session. For the functional experiment, however, these percentages were manually calculated.
4. History screens have fixed/fake data, it has yet to be implemented.