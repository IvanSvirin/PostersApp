# SmartAirKey SDK

Набор для разработчика SmartAirKey (SmartAirKey SDK) позволяет встроить функциональность взаимодействия с оборудованием SmartAirkey (контроллеры доступа SimpleLock)  в любое приложение.

Взаимодействие с оборудованием SmartAirKey происходит по интерфейсу Bluetooth Low Energy(BLE), в том числе в фоновом режиме.

Предоставляемая библиотека поддерживает в приложении процесс, который реализует:
1. Подключение контроллеров по BLЕ. Поддержка жизненного цикла транспорта.
2. Протокол взаимодействия приложения и контроллера по BLE v2 и v3 (protobuf).
3. Прием и передача сообщений и команд между телефоном и контроллером. Статусы, команды открытия, настройки контроллера.

Для управления доступом к контроллерам используются цифровые ключи, получаемые с сервера SmartAirKey для каждого пользователя.


## Процесс работы

Процесс работы с библиотекой состоит из следующих операций:
1. Подготовка приложения 
2. Инициализация
3. Передача в библиотеку набора цифровых ключей для контроллеров
4. Получение статусов от контроллеров
5. Передача команд для контроллеров

Контроллеры могут находиться в статусах ”Не подключен”, ”Подключение”, “Закрыт”/”Открыт”. Если контроллер находится в статусе “Не подключен”, то передача команд невозможна (например, телефон и контроллер находятся вне зоны подключения по BLE).

### 1. Подготовка приложения

Перед началом работы необходимо поместить файл библиотеки bluetoothcomm.aar в папку /libs вашего проекта.

Затем добавить разрешение android.permission.FOREGROUND_SERVICE в файл AndroidManifest.xml вашего проекта

    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        package="com.yourapppackagename">

        <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

        <application
            ...
        </application>

    </manifest>

И, наконец, добавить следующие зависимости в файл build.gradle, который относится к модулю:

    dependencies {
        //jetpack
        implementation 'androidx.core:core-ktx:1.8.0'
        implementation 'androidx.appcompat:appcompat:1.4.2'
        implementation 'com.google.android.material:material:1.6.1'
        implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
        implementation 'androidx.preference:preference:1.2.0'

        //lib
        implementation fileTree(dir: 'libs', include: ['*.aar', '*.jar'], exclude: [])

        //rx
        implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
        implementation 'io.reactivex:rxandroid:1.2.1'

        //protocol buffers
        api "com.squareup.wire:wire-runtime:4.4.0"

        //time format
        implementation 'joda-time:joda-time:2.10.14'

        //logs
        implementation 'org.slf4j:slf4j-api:1.7.30'
    }


### 2. Инициализация

Сначала необходимо создать инстанс библиотеки:  

**val sdkInstance = AirKeySmartDeviceCore.getInstance()**

Далее вызвать метод инициализации библиотеки, в который как аргумент передается контекст андроид приложения:  

**sdkInstance.init(context)**

Затем через инстанс можно вызвать любой из методов библиотеки, которые описаны ниже. В частности можно передать
имя приложения, которое будет отображаться в уведомлении:  

**sdkInstance.addAppName("YourAppName")**

    val sdkInstance = AirKeySmartDeviceCore.getInstance()
    sdkInstance.init(context)
    sdkInstance.addAppName("YourAppName")

### 3. Передача в библиотеку набора цифровых ключей для контроллеров

Следующим шагом необходимо добавить в библиотеку цифровые ключи для контроллеров, которыми предстоит управлять,
используя данную библиотеку:

**fun addCryptoKeys(cryptoKeys: List\<CryptoKeyDto>)**

Все используемые модели данных приведены в соответствующем разделе (см. __Модели данных__).

    val cryptoKeyDto = CryptoKeyDto(...)
    sdkInstance.addCryptoKeys(listOf(cryptoKeyDto))

Подробный пример создания объекта CryptoKeyDto показан в следующем разделе (см. __Пример использования__).

### 4. Получение статусов от контроллеров

После того как мы передали в библитеку цифровые ключи мы можем подписаться на коллекцию соответствующих им замков:

**fun locksObservable(): Observable\<Collection\<Lock>>**

Метод возвращает Observable, подписавшись на который мы получаем актуальную  коллекцию замков(контроллеров) 
после каждой передачи набора цифровых ключей в библиотеку. Чтобы подписаться на изменения статуса конкретного замка,
необходимо использовать метод  
fun getStateChanged(): Observable\<LockDeviceState>  
класса Lock

    sdkInstance.locksObservable().subscribe { locks ->
                locks.toMutableList()[0].getStateChanged()
                    .subscribe { state ->
                        //do smth with state
                    }
            }
    
### 5. Передача команд для контроллеров
Управляющие команды включают в себя команду открытия замка и команду настроек автоокрытия.

Открытие замка:
    
**fun openLockBl(lockId: String)**

где в качестве аргумента передается идентификатор замка  


    sdkInstance.openLockBl(cryptoKeyDto.id)

Передача настроек автоокрытия:
    
**fun updateLockSettings(lockId: String, settings: LockSettings)**

где в качестве аргумента передаются идентификатор замка и новые настройки

    var settings = LockSettings(SmartKeySettingsModel(cryptoKeyDto))
    settings.autoOpen = true
    settings.setKeepDoorOpened(10000) // in ms
    settings.activeDistance = 33
    sdkInstance.updateLockSettings(cryptoKeyDto.id, settings)

Настройки автоокрытия включают в себя такие параметры как включение автооткрытия, 
время нахождение замка в открытом состоянии и дистанцию открытия.


## Пример использования

    //инициализация
    val sdkInstance = AirKeySmartDeviceCore.getInstance()
    sdkInstance.init(context)
    sdkInstance.addAppName("YourAppName")

    // создание и добавление цифрового ключа
    val bluetoothLeLockTransportDto = BluetoothLeLockTransportDto(
        deviceName = "PZ7ZWRFYAPSFE24",
        macAddress = "58:63:56:D2:25:CE",
        serviceUuid = "0bd51666-e7cb-469b-8e4d-2742f1ba77cc",
        charDataUuid = "e7add780-b042-4876-aae1-112855353cc1",
        charRssiUuid = "9de0d5aa-d747-4bb1-aa64-c2c5f8ffdfdc",
    )
    bluetoothLeLockTransportDto.type = TransportDto.TransportsType.BlueToothLe
    val transports = TransportDto()
    transports.bluetoothLe = bluetoothLeLockTransportDto
    val lockDto = LockDto(
        id = "BWFSJB7QARDGBJI",
        transports = transports,
    )
    val signatureKeyDto = SignatureKeyDto(
        salt = "QW5LN291MkN6ckhu",
        token = "KVZW+3B1fNBXz+1EHYV/g80qyxRnbL0vCdQTcPX9cOw=",
        algorithm = "hmacSha256",
    )
    val keySettingsDto = KeySettingsDto(
        keepDoorOpened = 4000,
        useMethod1 = false,
        useMethod2 = false,
        activeDistance = 17,
        autoOpen = true,
        autoClose = false,
        keepOpenedWhileStayingNear = true,
    )
    val usageDto = UsageDto(
        listOf(
            RestrictionDto(
               key = "AutoOpenEnabled",
               value = "True",
               type = "boolean",
            ),
            RestrictionDto(
                key = "KeepOpenedEnabled",
                value = "True",
                type = "boolean",
            ),
            RestrictionDto(
                key = "MaxOpenedTime",
                value = "60000",
                type = "timeSpan",
            ),
            RestrictionDto(
                key = "MinOpenedTime",
                value = "3000",
                type = "timeSpan",
            ),
            RestrictionDto(
                key = "MaxActiveDistance",
                value = "100",
                type = "int",
            ),
            RestrictionDto(
                key = "MinActiveDistance",
                value = "0",
                type = "int",
            ),
        )
    )
    val periodDto = PeriodDto(
        from = "2022-08-01T00:00:00.0000+00:00",
        till = "2022-09-15T23:59:59.0000+00:00",
    )
    val cryptoKeyDto = CryptoKeyDto(
        id = "9f7b7b0f-d937-4574-8c61-aee400353c45",
        title = "Тестовый BWFSJB7QARDGBJI",
        lock = lockDto,
        signatureKey = signatureKeyDto,
        accessToken = "CgIIAxIuGiwKDEFuSzdvdTJDenJIbhDbi5T//f////8BGAEggJOKcij/3PxzMgMIHwU4ARogyK9FacVeIkywGB8mnkd4FZFIkP1A9i1zY0qZnKh00Uo=",
        settings = keySettingsDto,
        usage = usageDto,
        period = periodDto,
        created = "2022-08-01T00:13:49.4577+00:00",
    )
    sdkInstance.addCryptoKeys(listOf(cryptoKeyDto))

    // получение статуса замка
    sdkInstance.locksObservable()
        .subscribe { locks ->
            locks.toMutableList()[0].getStateChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    // do smth with state
                }
        }

    // открытие замка
    sdkInstance.openLockBl(cryptoKeyDto.id)

    // обновление настроек замка
    var settings = LockSettings(SmartKeySettingsModel(cryptoKeyDto))
    settings.autoOpen = true
    settings.setKeepDoorOpened(10000) // in ms
    settings.activeDistance = 33
    sdkInstance.updateLockSettings(cryptoKeyDto.id, settings)


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

    enum class LockDeviceState {
        Opened, Closed, Unavailable, Connecting, Insight, Unauthorized;

        fun oneOf(vararg states: LockDeviceState): Boolean {
            for (state in states) {
                if (this == state) {
                    return true
                }
            }
            return false
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
