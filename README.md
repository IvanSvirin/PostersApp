# SmartAirKey SDK

Набор для разработчика SmartAirKey (SmartAirKey SDK) позволяет встроить функциональность взаимодействия с оборудованием SmartAirkey (контроллеры доступа SimpleLock)  в любое приложение.

Взаимодействие с оборудованием SmartAirKey происходит по интерфейсу BLE, в том числе в фоновом режиме.

Предоставляемая библиотека поддерживает в приложении процесс, который реализует:
1. Подключение контроллеров по BLЕ. Поддержка жизненного цикла транспорта.
2. Протокол взаимодействия приложения и контроллера по BLE v2 и v3 (protobuf). Стэйт-машина протокола.
3. Прием и передача сообщений и команд между телефоном и контроллером. Статусы, команды открытия, настройки контроллера.

Для управления доступом к контроллерам используются цифровые ключи, получаемые с сервера SmartAirKey для каждого пользователя.


## Процесс работы

Процесс работы с библиотекой состоит из следующих операций:
1. Инициализация 
2. Передача в библиотеку набора цифровых ключей для контроллеров
3. Получение статусов от контроллеров
4. Передача команд для контроллеров

Контроллеры могут находиться в статусах “Подключен”/”Не подключен”, “Закрыт”/”Открыт”. Если контроллер находится в статусе “Не подключен”, то передача команд не возможна (например, телефон и контроллер находятся вне зоны подключения по BLE).

### 1. Инициализация

### 2. Передача в библиотеку набора цифровых ключей для контроллеров

### 3. Получение статусов от контроллеров

### 4. Передача команд для контроллеров

1. **openLock()** - открытие замка 

2. **updateSettings()** - передача настроек 


## Модели данных

data class CryptoKeyDto(
    var id: String = "",
    var title: String = "",
    var lockId: String? = "",
    var lock: LockDto = LockDto(),
    var signatureKey: SignatureKeyDto? = null,
    var signatureKeyV3: SignatureKeyV3Dto? = null,
    var accessToken: String = "",
    var accessTokenV3: String? = null,
    var authCookie: Cookie? = null,
    var settings: KeySettingsDto = KeySettingsDto(),
    var usage: UsageDto = UsageDto(),
    var period: PeriodDto = PeriodDto(),
    var created: String = "",
    var modified: String = "",
    var compositeKeyId: String = "",
    var isEncrypted: Boolean = false,
)

## Contributors

- [Иван Свирин](https://github.com/ivansvirin)


## License and copyright
Copyright (c) 2022 Airkey

Licensed under the Apache 2.0 license
