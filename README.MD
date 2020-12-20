SKILL ARTICLES
==============
Приложение позволяющее просматривать статьи по ИТ тематике, а так же оценивать и комментировать их.

## Основные особенности
* Server driven UI (список статей и их содержимое формируется на сервере и направляется в приложение в виде [Markdown](https://ru.wikipedia.org/wiki/Markdown) разметки)
* Поиск статей по названию и по тэгам
* Поиск по содержимому статей с вожможностью навигации
* Фильтр статей по предустановленным категориям
* Возможность оставлять комментарии под статьями и ставить лайки
* Локальный кэш данных (приложение хранит во внутренней базе список загруженных статей и, при добавлении статьи в закладки, делает её доступной для оффлайн просмотра)
* Поддержка тем (пользователь может выбрать дневную/ночную темы, и, при необходимости, увеличенный шрит)
* Взаимодействие с сервером посредствам Rest API с использовонием GET, POST и PUT запросов и механизмов access и refresh токенов
* Русская и ангийская локализации
* Регистрация и авторизация пользователя
* Управления профилем пользователя (обновление данных пользователя, создание и загрузка на сервер аватара)


## Стек
* Kotlin
* MVVM
* Dagger 2, Hilt
* Kotlin Coroutines, Live Data
* Navigation Components
* Room
* Paging library
* Retrofit 2, OkHttp 3, moshi
* Glide
* CustomViews
* Lottie

## Скриншоты
<p align="left">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/1.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/5.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/2.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/6.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/3.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/4.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/7.png">
  <img width="216" height="456" src="https://raw.githubusercontent.com/AlekseyYakovlev/SkillArticles/master/screenshots/8.png">
</p>