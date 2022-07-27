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

Метод возвращает Observable, подписавшись на который мы получаем актуальную  коллекцию замков(контроллеров) 
после каждой передачи набора цифровых ключей в библиотеку. Чтобы подписаться на изменения статуса конкретного замка,
необходимо использовать метод  
fun getStateChanged(): Observable\<LockDeviceState>  
класса Lock
    
### 4. Передача команд для контроллеров

Открытие замка:
    
**fun openLockBl(lockId: String)**

где в качестве аргумента передается идентификатор замка  


Передача настроек автоокрытия:
    
**fun updateLockSettings(lockId: String, settings: LockSettings)**

где в качестве аргумента передаются идентификатор замка и новые настройки

## Модели данных

`data class CryptoKeyDto(
    var id: String = "",
    var title: String = "",
    var lock: LockDto = LockDto(),
    var signatureKey: SignatureKeyDto? = null,
    var accessToken: String = "",
    var settings: KeySettingsDto = KeySettingsDto(),
    var usage: UsageDto = UsageDto(),
    var period: PeriodDto = PeriodDto(),
    var created: String = "",
)

data class LockDto(
    var id: String = "",
    var transports: TransportDto = TransportDto(),
)

class TransportDto {
    var bluetooth: BluetoothLockTransportDto? = null
    var bluetoothLe: BluetoothLeLockTransportDto? = null

    enum class TransportsType(var type: Int) {
        BlueToothLe(1), BlueTooth(2), Unknown(3);
    }

    companion object {
        fun getType(type: Any?): TransportsType {
            for (alg in TransportsType.values()) {
                if (type is Number && alg.type == type.toInt()) {
                    return alg
                } else if (alg.name == type) {
                    return alg
                }
            }
            throw ArrayIndexOutOfBoundsException("algorithm")
        }
    }
}

open class LockTransportDto {
    var type: TransportDto.TransportsType = TransportDto.TransportsType.Unknown
}

data class BluetoothLockTransportDto(
    var deviceName: String = "",
    var macAddress: String = "",
    var secureUuid: String = "",
    var insecureUuid: String = "",
) : LockTransportDto()

data class BluetoothLeLockTransportDto(
    var deviceName: String = "",
    var macAddress: String = "",
    var serviceUuid: String = "",
    var charDataUuid: String = "",
    var charRssiUuid: String = "",
) : LockTransportDto()

data class SignatureKeyDto(
    var salt: String = "",
    var token: String = "",
    @SerializedName("algorithm")
    val algorithm: Any?,
) {
    fun getAlgorithm(): AlgorithmDtoType {
        for (alg in AlgorithmDtoType.values()) {
            if (algorithm is Number && alg.type == algorithm.toInt()) {
                return alg
            } else if (alg.name == algorithm) {
                return alg
            }
        }
        throw ArrayIndexOutOfBoundsException("algorithm")
    }

    enum class AlgorithmDtoType(var type: Int) {
        hmacSha1(1), hmacSha256(2);
    }
}

data class KeySettingsDto(
    var keepDoorOpened: Int = 0,
    var useMethod1: Boolean = false,
    var useMethod2: Boolean = false,
    var activeDistance: Int = 0,
    var autoOpen: Boolean = false,
    var autoClose: Boolean = false,
    var keepOpenedWhileStayingNear: Boolean = false,
)

data class UsageDto(
    var restrictions: List<RestrictionDto> = ArrayList<RestrictionDto>()
)

data class RestrictionDto(
    var key: String = "",
    var value: String = "",
    var type: String = "",
)

data class PeriodDto(
    var from: String = "",
    var till: String = "",
)`

## Contributors

- [Иван Свирин]()


## License and copyright
Copyright (c) 2022 Airkey

Licensed under the Apache 2.0 license
