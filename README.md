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

Сначала необходимо создать инстанс библиотеки:  

**val sdkInstance = AirKeySmartDeviceCore.getInstance()**

Далее вызвать метод инициализации библиотеки, в который как аргумент передается контекст андроид приложения:  

**sdkInstance.init(context)**

Затем через инстанс можно вызвать любой из методов библиотеки, которые описаны ниже. В частности можно передать
имя приложения, которое будет отображаться в уведомлении:  

**sdkInstance.addAppName("YourAppName")**

### 2. Передача в библиотеку набора цифровых ключей для контроллеров

**fun addCryptoKeys(cryptoKeys: List\<CryptoKeyDto>)**

(все используемые модели данных приведены в следующем разделе)

### 3. Получение статусов от контроллеров

**fun locksObservable(): Observable\<Collection\<Lock>>**

Метод возвращает события изменений, произошедших в коллекции замков(контроллеров), из которых нас в первую очередь интересует текущий
статус замка, который может быть получен вызовом метода  
fun getStateChanged(): Observable\<LockDeviceState>  
класса Lock, который возвращает
событие изменения статуса данного замка(контроллера)
    
### 4. Передача команд для контроллеров

Открытие замка:
    
**fun openLockBl(lockId: String)**

Передача настроек автоокрытия:
    
**fun updateLockSettings(lockId: String, settings: LockSettings)**


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
