# OutdoorActivity2024 (Project 2/3 of Methodia 2024 Java 11 Workshop/Self-training)

## Игра на федербал

Напишете програма, която определя възможните времеви интервали за игра на федербал на открито пространство, в които метеорологичните условия са подходящи. Примерни стойности:
- поривите на вятъра трябва да са по-малки от 5 км/ч;
- вероятността за валежи да бъде по-малка от 50%;
- трябва да е светло (след изгрев слънце и преди залез слънце);
- температурата да бъде между 10 и 30 градуса Целзий;
- трябва да има поне два часа с тези условия;
- уикендите и облачното време (над 50% облачна покривка) са за предпочитане.

Изведете възможните времеви интервали до 2 дена напред. Трябва да изведете в конзолата диапазон от часове за всеки ден, в който има подходящи условия.
Пример за форматиране на изходните данните:
```json
[
{
"date": "2023-05-15",
"hours": ["10-13", "15-19"]
},
{
"date": "2023-05-16",
"hours": ["08-11", "14-16", "17-20"]
},
{
"date": "2023-05-17",
"hours": ["14-20"]
}
]
```

Информацията за часовете е за всеки кръгъл час, т.е."10-13" означава, че от 10:00 до 13:00 часа е подходящо за игра.
За нуждите на задачата можете да ползвате, която и да е безплатна уеб услуга, която предоставя прогноза за времето. Една от тях е https://www.weatherapi.com/. Разгледайте и разучете какво Ви предоставя API на секция Forecast API.

````diff
+ ===================================== SOLUTION ==================================== 
+ Implemented two interchangable weather services (through ForecastService interface):
+ with WehaterAPI and OpenWeatherMap API
````
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot1.png)

## Допълнение 1
Потребителят да може да дефинира файл (ръчно), където да описва условията за различни видове спорт. Форматът на този файл го определете вие. Изходът на програмата вече ще извежда подходящите интервали за всеки вид спорт.

````diff
+ ===================================== SOLUTION ==================================== 
+ CSV file format for the inputs, by my own design
+ Implemented second InputCSV interchangable input (through DataInputer interface)
+ and "com.opencsv" reader (default Scanner and BufferReader won't parse the csv correctly)
````
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot2.png)
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot3.png)

## Допълнение 2
Програмата да може да се стартира в наблюдаващ режим, изпълнявайки се продължително време, тоест ще е вид service. През определен интервал ще извлича прогнозата за времето. При достигане на определени критерии, ще изпраща мейл. Критериите отново потребителят ги задава чрез файл. Например, да прати мейл ако за следващия уикенд има подходящи условия за даден спорт в определен часови интервал. Опционално може да са и всички почивни дни. За целта [този](https://bulgaria.workingdays.org/setup) или подобен сървис би ви свършил работа.

````diff
+ ===================================== SOLUTION ==================================== 
+ Continuous running / automation mode implemented through two interchangable services:
+ The standard Java Executors.newSingleThreadScheduledExecutor() singleton 
+ and the 'org.quartz-scheduler' cron job runner;
+ The mail notification implemented through 'com.sun.mail:jakarta.mail' service, using
+ two interchangable SMTP configurations: Mailtrap (mailtrap.io) and Gmail (mail.google.com)
````
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot4.png)
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot5.png)

## Допълнение 3
При откриване на подходящи условия за спорт, програмата трябва да създаде събитие в Google Calendar.
Детайлите около събитието можете сами да ги определите - създаване/редактиране/изтриване на събитие, припокриващи се събития, цвят, участници, местоположение, нотификация и т.н.

````diff
+ ===================================== SOLUTION ==================================== 
+ Running as an app on the client's device, asking for permission (OAuth2)
+ and creating calendar for the app, where new events are scheduled.
+ On an hourly basis the app checks the weather API for changes and
+ eventually updates the calendar events and sends mail notification.
+ Unit Tests with Mockito for the SuitableWeatherService and  
+ for the UpdaterService (using FilePersistService singleton) - 'mockito-inline'.
````
![](https://github.com/Stefan-B-K/Java_OutdoorActivity2024/blob/main/src/main/resources/images/Screenshot6.png)