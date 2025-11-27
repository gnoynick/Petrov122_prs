# Петров Артём 4ИСИП-122

## Описание приложения

Было создано безопасное мобильное приложение для аутентификации, разработанное на Kotlin для Android, реализующее современные архитектурные паттерны (MVVM, Clean Architecture) с комплексными функциями управления пользователями.

---

## ПР1: Создание локальной базы данных

### Описание
Реализация надежной системы локальной базы данных с использованием Room (обертка для SQLite) для постоянного хранения данных.

### Ключевые файлы:

**Ядро базы данных:**
- `app/src/main/java/com/example/authapp/data/database/AppDatabase.kt`
  - Главный класс базы данных, паттерн Singleton
  - Конфигурация и инициализация базы данных
  - Управление версиями и миграциями

- `app/src/main/java/com/example/authapp/data/database/Converters.kt`
  - Конвертеры типов для объектов Date
  - Позволяет хранить сложные типы данных

**Объекты доступа к данным (DAO):**
- `app/src/main/java/com/example/authapp/data/dao/UserDao.kt`
  - Операции CRUD для пользователей
  - Запросы пользователей (по email, имени, ID)
  - Обновления пароля и профиля

- `app/src/main/java/com/example/authapp/data/dao/SessionDao.kt`
  - Запросы управления сессиями
  - Валидация и очистка токенов
  - Обработка сессий для конкретных устройств

**Сущности базы данных:**
- `app/src/main/java/com/example/authapp/data/entities/UserEntity.kt`
  - Схема таблицы пользователей с индексами
  - Поля: username, email, password_hash, salt, личная информация
  - Уникальные ограничения на email и имя пользователя

- `app/src/main/java/com/example/authapp/data/entities/UserSessionEntity.kt`
  - Таблица сессий с внешним ключом на пользователей
  - Поля: session_token, device_id, expiry_time
  - Каскадное удаление при удалении пользователя

**Используемые технологии:**
- Room Database Library
- SQLite
- Coroutines для асинхронных операций
- DataStore для настроек

---

## ПР2: Авторизация и регистрация

### Описание
Полная система аутентификации с безопасным хешированием паролей, управлением сессиями и валидацией пользователей.

### Ключевые файлы:

**Слой репозитория:**
- `app/src/main/java/com/example/authapp/data/repository/AuthRepositoryImpl.kt`
  - Логика регистрации с валидацией
  - Аутентификация при входе
  - Функциональность смены пароля
  - Удаление аккаунта
  - Интеграция управления сессиями

**Доменный слой:**
- `app/src/main/java/com/example/authapp/domain/repository/AuthRepository.kt`
  - Интерфейс, определяющий операции аутентификации
  - Контракт для реализации репозитория

- `app/src/main/java/com/example/authapp/domain/models/AuthResult.kt`
  - Sealed-классы для результатов аутентификации (Успех/Ошибка)
  - Управление состоянием аутентификации
  - Модель профиля пользователя

**Утилиты безопасности:**
- `app/src/main/java/com/example/authapp/domain/utils/PasswordManager.kt`
  - Хеширование паролей PBKDF2 с солью
  - Проверка сложности пароля (СЛАБЫЙ/СРЕДНИЙ/СИЛЬНЫЙ)
  - Генерация безопасной случайной соли
  - Проверка пароля

- `app/src/main/java/com/example/authapp/domain/utils/SessionManager.kt`
  - Генерация токенов сессии (на основе UUID)
  - Управление сроком действия сессий (24ч или 30 дней)
  - Отслеживание ID устройства
  - Валидация сессий

- `app/src/main/java/com/example/authapp/domain/utils/PreferencesManager.kt`
  - Безопасное хранение токенов сессии
  - Пользовательские настройки (запомнить меня)
  - Реализация DataStore

**ViewModel:**
- `app/src/main/java/com/example/authapp/presentation/viewmodels/AuthViewModel.kt`
  - Оркестрация регистрации и входа
  - Валидация форм (email, пароль, имя пользователя)
  - Управление состоянием (Загрузка/Успех/Ошибка)
  - Отслеживание пользовательских сессий

**Реализованные функции:**
- Безопасное хеширование паролей (PBKDF2 с SHA-256)
- Хранение паролей на основе соли
- Управление токенами сессии
- Функциональность "Запомнить меня" (30-дневные сессии)
- Валидация email и имени пользователя
- Проверка сложности пароля
- Сессии для конкретных устройств

---

## ПР3: Использование элементов дизайна

### Описание
Современная реализация Material Design 3 с комплексными UI-компонентами и улучшениями пользовательского опыта.

### Ключевые файлы:

**Файлы макетов:**
- `app/src/main/res/layout/activity_main.xml`
  - CoordinatorLayout для продвинутой прокрутки
  - Контейнер NavHostFragment

- `app/src/main/res/layout/fragment_login.xml`
  - TextInputLayout с переключением видимости пароля
  - Material-кнопки (контурные, текстовые, заполненные)
  - Checkbox с кастомным стилем
  - ProgressBar для состояний загрузки
  - ScrollView для адаптивного дизайна

- `app/src/main/res/layout/fragment_register.xml`
  - Несколько TextInputLayout с валидацией
  - Отображение сообщений об ошибках
  - Организация полей формы
  - Кнопки действий (регистрация, демо, очистка)

- `app/src/main/res/layout/fragment_main.xml`
  - Простой макет панели управления
  - Дизайн с центрированным контентом

**Функции улучшения UI:**
- `app/src/main/java/com/example/authapp/presentation/utils/TooltipExtensions.kt`
  - Кастомная система всплывающих подсказок
  - Всплывающие подсказки на основе Material Card
  - Функциональность автоматического скрытия
  - Помощь, активируемая фокусом

- `app/src/main/java/com/example/authapp/domain/utils/TooltipManager.kt`
  - Управление состоянием всплывающих подсказок
  - Руководство для новых пользователей
  - Сохранение настроек всплывающих подсказок

**Ресурсы:**
- `app/src/main/res/values/colors.xml`
  - Цветовая палитра Material Design
  - Цвета стилей всплывающих подсказок
  - Согласованность темы

**Используемые элементы дизайна:**
- Компоненты Material Design 3
- TextInputLayout с плавающими метками
- Переключение видимости пароля
- Material-кнопки (3 варианта)
- Material Cards для всплывающих подсказок
- ProgressBar для асинхронных операций
- Elevation и тени
- Адаптивные ScrollViews
- Интеграция иконок (endIconMode)
- Визуализация состояний ошибки

**UX-функции:**
- Валидация форм с inline-ошибками
- Индикаторы загрузки
- Toast-уведомления для обратной связи
- Автозаполнение демо-данными
- Опция гостевого доступа
- Система руководства через всплывающие подсказки

---

## ПР4: Навигация между страницами

### Описание
Реализация Android Navigation Component для плавных переходов между фрагментами и навигационных потоков.

### Ключевые файлы:

**Конфигурация навигации:**
- `app/src/main/res/navigation/nav_graph.xml` 
  - Граф навигации, определяющий все направления
  - Соединения действий между фрагментами
  - Конфигурация стартового направления

**Activity:**
- `app/src/main/java/com/example/authapp/presentation/activities/MainActivity.kt`
  - Архитектура single-activity
  - Хостинг NavHostFragment

**Навигация фрагментов:**
- `app/src/main/java/com/example/authapp/presentation/fragments/LoginFragment.kt`
  - Навигация к RegisterFragment
  - Навигация к MainFragment (после входа)
  - Гостевая навигация
  - `findNavController().navigate(R.id.action_*)`

- `app/src/main/java/com/example/authapp/presentation/fragments/RegisterFragment.kt`
  - Навигация к LoginFragment
  - Навигация к MainFragment (после регистрации)
  - Обработка обратной навигации

- `app/src/main/java/com/example/authapp/presentation/fragments/MainFragment.kt`
  - Навигация к LoginFragment (после выхода)
  - Навигация домашнего экрана

**Функции навигации:**
- Навигация на основе фрагментов
- Type-safe навигационные действия
- Управление back stack
- Поддержка глубоких ссылок (архитектурная готовность)
- Навигация после успешной аутентификации
- Условная навигация (гость vs аутентифицированный)
  
---

## ПР5: Создание подсказок

### Описание
Интеллектуальная система всплывающих подсказок, предоставляющая контекстную помощь и руководство пользователя throughout приложения.

### Ключевые файлы:

**Управление всплывающими подсказками:**
- `app/src/main/java/com/example/authapp/domain/utils/TooltipManager.kt`
  - Сохранение через DataStore
  - Отслеживание состояния отображения подсказок
  - Глобальное включение/отключение
  - Обнаружение новых пользователей
  - Функциональность сброса для тестирования
  - 10+ предопределенных ключей подсказок

**Отображение подсказок:**
- `app/src/main/java/com/example/authapp/presentation/utils/TooltipExtensions.kt`
  - Функции расширения View
  - Стилизация подсказок в Material Design
  - Автоматическое скрытие с настраиваемой длительностью
  - Скрытие по касанию
  - Расчет позиции
  - Всплывающие подсказки на основе MaterialCardView

**Интеграция подсказок:**
- `app/src/main/java/com/example/authapp/presentation/fragments/LoginFragment.kt`
  - Всплывающие подсказки, активируемые фокусом на полях ввода
  - Последовательное отображение подсказок
  - Руководство по кнопке демо
  - Объяснение "Запомнить меня"

- `app/src/main/java/com/example/authapp/presentation/fragments/RegisterFragment.kt`
  - Всплывающие подсказки помощи для полей формы
  - Руководство по сложности пароля
  - Подсказки для email и имени пользователя
  - Объяснение подтверждения пароля

**Внедрение зависимостей:**
- `app/src/main/java/com/example/authapp/di/DatabaseModule.kt`
  - Провайдер TooltipManager
  - Паттерн Singleton
  - Инъекция контекста приложения

На рисунке 5 представлена работа подсказок в приложении

<img width="328" height="706" alt="image" src="https://github.com/user-attachments/assets/4ecf0978-73e7-4d8b-9843-98ceb01e34b9" />

Рисунок 5

---

## Дополнительные архитектурные компоненты

**Внедрение зависимостей:**
- `app/src/main/java/com/example/authapp/di/DatabaseModule.kt`
  - Настройка Hilt/Dagger
  - Провайдеры Singleton для всех утилит
  - Предоставление экземпляра базы данных
  - Инъекция DAO

**Стек технологий:**
- Kotlin
- Android Jetpack (Room, Navigation, ViewModel, DataStore)
- Coroutines & Flow
- Material Design 3
- Hilt для внедрения зависимостей
- Архитектура MVVM
- Принципы Clean Architecture

