# Something
#### 할 일을 자주 깜빡하는 사람들을을 위한 ToDo 앱입니다.
#### Android ToDo Application for a people who often forgets what to do
https://play.google.com/store/apps/details?id=com.taetae98.something

## ⚡ Features

### 잠금해제시 확인
#### 핸드폰을 잠금해제시 앱이 자동으로 실행되어 ToDo 목록을 확인할 수 있습니다. (설정으로 기능을 On/Off 가능)
#### The app will automatically launch when you unlock your phone, allowing you to check the ToDo list. (Settings allow the function to be turned on/off)
<img src="./readme/unlock_store.png" alt="unlock.png" width="80%">

### 알람창으로 확인
#### 중요한 일을 알림창으로 확인할 수 있습니다. (ToDo마다 설정가능)
#### You can check important things in the notification window. (Can be set per ToDo)
<img src="./readme/notification_store.png" alt="notification.png" width="80%">

### 달력으로 확인
#### 달력으로 한눈에 ToDo 목록을 확인할 수 있습니다.
#### You can check the ToDo list at a glance on your calendar.
<img src="./readme/calendar_store.png" alt="calendar.png" width="80%">

### 서랍별로 확인
#### 서랍별로 연관있는 ToDo 목록을 만들 수 있습니다.
#### You can create a list of associated ToDos by drawer.
<img src="./readme/drawer_store.png" alt="drawer.png" width="80%">

### 다크모드 지원
#### Blue, Dark 테마를 설정에서 선택할 수 있으며 System 설정에 반응형으로 Theme를 변경할 수 있습니다.
#### You can select the Blue, Dark theme from Settings and change the Theme to System Settings to Reactive.
<img src="./readme/dark_store.png" alt="dark.png" width="80%">

### 간단하고 깔끔한 UI
#### Jetpack Architecture Navigation과 DrawerLayout을 사용하여 간단하고 깔끔한 UI를 제공하며 UI 대상간 이동에서 Animation을 제공합니다.
#### Use Jetpack Architecture Navigation and Drawer Layout to provide a simple and clean UI and provide animation in the movement between UI targets.
<img src="./readme/ui_store.png" alt="ui.png" width="80%">

### System에 맞는 언어 제공
#### String Resource를 사용해서 System에 맞는 언어를 제공합니다.
<img src="./readme/language.png" alt="ui.png" width="80%">

## 📘 Library
### RecyclerView : ToDo, Drawer 목록을 표현할 때 사용
* setHasStableIds와 ItemCallback을 사용하여 성능을 향상시킴
* ItemDecoration을 사용하여 ViewHolder간 Margin을 일정하게 처리
* ItemTouchHelper를 사용하여 ToDo를 Swipe시 완료/삭제 처리

### Room : ToDo, Drawer를 저장할 때 사용
* AppDatabase Class를 Singleton Pattern을 적용해서 사용
* addCallback을 통해 처음 사용하는 사용자에게 앱 사용법을 ToDo 목록으로 설명
* ForeignKey를 통해 ToDo와 Drawer의 관계를 설정

### ViewModel : ToDo, Drawer를 ViewModel로 관리
* ViewModel, LiveData를 결합하여 ToDo와 Drawer를 관리

### Navigation : UI 대상을 전환할 때 사용
* Fragment간 전환시 Animation 적용
* DrawerLayout, NavigationView를 Navigation과 결합하여 사용

### Hilt : Room, DataStore, ToDoAdapter, DrawerAdapter 등 여러 항목을 DI로 전달
* ToDoRepository, DrawerRepository와 ViewModel을 결합하여 Room을 효율적으로 접근
* SettingRepository로 DataStore를 효율적으로 접근

### DataStore : 사용자의 설정을 저장할 때 사용
* SharedPreferences의 단점을 보완한 DataStore를 사용하여 사용자의 설정을 저장

## 😊 Screenshot
<img src="./readme/todo_fragment.png" alt="todo_fragment.png" width="25%"><img src="./readme/todo_edit_fragment.png" alt="todo_edit_fragment.png" width="25%"><img src="./readme/calendar_fragment.png" alt="calendar_fragment.png" width="25%"><img src="./readme/todo_date_dialog.png" alt="todo_date_dialog.png" width="25%">  
<img src="./readme/drawer_fragment.png" alt="drawer_fragment.png" width="25%"><img src="./readme/drawer_edit_fragment.png" alt="drawer_edit_fragment.png" width="25%"><img src="./readme/finished_todo_fragment.png" alt="finished_todo_fragment.png" width="25%"><img src="./readme/setting_fragment.png" alt="setting_fragment.png" width="25%">  
<img src="./readme/drawer.png" alt="drawer.png" width="25%"><img src="./readme/darkmode.png" alt="darkmode.png" width="25%"><img src="./readme/notification.png" alt="notification.png" width="25%">
