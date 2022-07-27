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

    locksObservable().subscribe { locks ->
                locks.toMutableList()[0].getStateChanged()
                    .subscribe { state ->
                        //do smth with state
                    }
            }
    
### 4. Передача команд для контроллеров

Открытие замка:
    
**fun openLockBl(lockId: String)**

где в качестве аргумента передается идентификатор замка  


Передача настроек автоокрытия:
    
**fun updateLockSettings(lockId: String, settings: LockSettings)**

где в качестве аргумента передаются идентификатор замка и новые настройки

## Модели данных

    data class CryptoKeyDto(
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
    )

    class Lock(private var lockDevice: LockDevice) {

        fun getId(): UUID {
            return lockDevice.getId()
        }

        fun getStateChanged(): Observable<LockDeviceState> {
            return lockDevice.getStateChanged()
        }

        fun getKey(): CryptoKeyModel {
            return lockDevice.getItsKey()
        }

        fun getState(): LockDeviceState {
            return lockDevice.getState()
        }

        fun getUsage(): LockUsage {
            return if (lockDevice.getItsKey() == null) {
                LockUsage()
            } else LockUsage(lockDevice.getItsKey().getUsage())
        }

        fun getTitle(): String {
            return lockDevice.getItsKey().getTitle()
        }

        fun getSettings(): LockSettings {
            return LockSettings(lockDevice.getSettings())
        }
    }

    class LockSettings(settings: SmartKeySettingsModel) {
        var settings: SmartKeySettingsModel

        var activeDistance: Int
            get() = settings.activeDistance
            set(activeDistance) {
                settings.activeDistance = activeDistance
            }

        val privateModel: SmartKeySettingsModel
            get() = settings
        var title: String
            get() = settings.title
            set(title) {
                settings.title = title
            }
        val keepDoorOpened: KeepDoorOpenedModel
            get() = settings.keepDoorOpened

        var autoOpen: Boolean
            get() = settings.autoOpen
            set(autoOpen) {
                settings.autoOpen = autoOpen
            }
        var keepOpenedWhileStayingNear: Boolean
            get() = settings.keepOpenedWhileStayingNear
            set(keepOpenedWhileStayingNear) {
                settings.keepOpenedWhileStayingNear = keepOpenedWhileStayingNear
            }
        var useGestureMethod2: Boolean
            get() = settings.useGestureMethod2
            set(useGestureMethod2) {
                settings.useGestureMethod2 = useGestureMethod2
            }
        var useGestureMethod1: Boolean
            get() = settings.useGestureMethod1
            set(useGestureMethod1) {
                settings.useGestureMethod1 = useGestureMethod1
            }

        fun setKeepDoorOpened(i: Int) {
            settings.setKeepDoorOpened(i)
        }

        init {
            this.settings = settings
        }
    }

    data class KeepDoorOpenedModel(var value: Int) {
        fun setVal(i: Int) {
            value = i
        }

        val valueSec: Int
            get() = value / 1000
    }

    class SmartKeySettingsModel(smartKey: CryptoKeyDto) {
        var keyId: UUID
        var autoOpen: Boolean
        var keepOpenedWhileStayingNear: Boolean
        var title: String
        private var autoClose: Boolean
        var useGestureMethod1: Boolean
        var useGestureMethod2: Boolean
        var activeDistance: Int
        private val _keepDoorOpened: KeepDoorOpenedModel

        private fun getAutoOpen(
            usage: KeyUsageModel,
            settings: KeySettingsDto
        ): Boolean {
            return settings.autoOpen &&
                    usage.getRestriction(RestrictionModel.Type.AutoOpenEnabled).boolValue
        }

        private fun getKeepOpened(
            usage: KeyUsageModel,
            settings: KeySettingsDto
        ): Boolean {
            return settings.keepOpenedWhileStayingNear &&
                    usage.getRestriction(RestrictionModel.Type.KeepOpenedEnabled).boolValue
        }

        private fun getActiveDistance(
            usage: KeyUsageModel,
            settings: KeySettingsDto
        ): Int {
            val maxActiveDistance: Int =
                usage.getRestriction(RestrictionModel.Type.MaxActiveDistance).intValue
            val minActiveDistance: Int =
                usage.getRestriction(RestrictionModel.Type.MinActiveDistance).intValue
            return if (settings.activeDistance > maxActiveDistance) {
                maxActiveDistance
            } else if (settings.activeDistance < minActiveDistance) {
                minActiveDistance
            } else {
                settings.activeDistance
            }
        }

        private fun getKeepDoorOpened(
            usage: KeyUsageModel,
            settings: KeySettingsDto
        ): KeepDoorOpenedModel {
            val maxOpenedTime: Int =
                usage.getRestriction(RestrictionModel.Type.MaxOpenedTime).timeSpan
            val minOpenedTime: Int =
                usage.getRestriction(RestrictionModel.Type.MinOpenedTime).timeSpan
            val keepDoorOpenedModel: KeepDoorOpenedModel =
                if (settings.keepDoorOpened > maxOpenedTime) {
                    KeepDoorOpenedModel(maxOpenedTime)
                } else if (settings.keepDoorOpened < minOpenedTime) {
                    KeepDoorOpenedModel(minOpenedTime)
                } else {
                    KeepDoorOpenedModel(settings.keepDoorOpened)
                }
            return keepDoorOpenedModel
        }

        fun setKeepDoorOpened(i: Int) {
            _keepDoorOpened.setVal(i)
        }

        val keepDoorOpened: KeepDoorOpenedModel
            get() = _keepDoorOpened

        init {
            val settings: KeySettingsDto =
                smartKey.settings
            val usage =
                KeyUsageModel(if (smartKey.usage == null) UsageDto() else smartKey.usage)
            keyId = UUID.fromString(smartKey.id)
            title = smartKey.title
            autoOpen = getAutoOpen(usage, settings)
            keepOpenedWhileStayingNear = getKeepOpened(usage, settings)
            autoClose = settings.autoClose
            useGestureMethod1 = settings.useMethod1
            useGestureMethod2 = settings.useMethod2
            _keepDoorOpened = getKeepDoorOpened(usage, settings)
            activeDistance = getActiveDistance(usage, settings)
        }
    }


## License and copyright
Copyright (c) 2022 Airkey

Licensed under the Apache 2.0 license
