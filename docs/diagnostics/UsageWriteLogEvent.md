# Неверное использование метода "ЗаписьЖурналаРегистрации" (UsageWriteLogEvent)

<!-- Блоки выше заполняются автоматически, не трогать -->
## Описание диагностики
<!-- Описание диагностики заполняется вручную. Необходимо понятным языком описать смысл и схему работу -->
Важно правильно с определенной дотошностью указывать основные параметры при записи в Журнал регистрации.

Недопустимо прятать исключения.
При обработке исключений обязательно нужно выполнять запись в журнал регистрации с полным представлением ошибки.
Для этого нужно добавить в комментарий события результат `ПодробноеПредставлениеОшибки(ИнформацияОбОшибке())`

Нельзя пропускать 2й параметр Уровень журнала регистрации. Если его не указать, по умолчанию 1С применит уровень ошибки Информация, и данная запись может потеряться в потоке записей.

Нельзя пропускать и 5й параметр - комментарий к событию записи в журнал регистрации.

## Примеры
<!-- В данном разделе приводятся примеры, на которые диагностика срабатывает, а также можно привести пример, как можно исправить ситуацию -->

Примеры неверного кода
```bsl
    ЗаписьЖурналаРегистрации("Событие");// ошибка
    ЗаписьЖурналаРегистрации("Событие", УровеньЖурналаРегистрации.Ошибка);// ошибка
    ЗаписьЖурналаРегистрации("Событие", УровеньЖурналаРегистрации.Ошибка, , );// ошибка
    ЗаписьЖурналаРегистрации("Событие", , , , ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));

    ЗаписьЖурналаРегистрации("Событие", УровеньЖурналаРегистрации.Ошибка, , , );// ошибка

    Попытка
      КодСервер();
    Исключение
      ЗаписьЖурналаРегистрации("Событие", УровеньЖурналаРегистрации.Ошибка, , ,
        ОписаниеОшибки()); // ошибка
      ЗаписьЖурналаРегистрации("Событие", УровеньЖурналаРегистрации.Ошибка, , ,
        "Комментарий 1"); // ошибка
    КонецПопытки;
```

Правильный код
```bsl
    Попытка
      КодСервер();
    Исключение

      ТекстОшибки = ПодробноеПредставлениеОшибки(ИнформацияОбОшибке());
      ЗаписьЖурналаРегистрации(НСтр("ru = 'Выполнение операции'"), УровеньЖурналаРегистрации.Ошибка, , ,
         ТекстОшибки);
    КонецПопытки;
```

## Примечание
В общем случае, если используется оператор ВызватьИсключение - то не следует дублировать запись ЖР вызовом метода `ЗаписьЖурналаРегистрации()`. Так как это приводит к двойной записи одной и той же ошибки в журнал регистрации. Необработанное исключение в любом случае будет неявно записано платформой в ЖР.

Правильно:
```bsl
Исключение
    ОтменитьТранзакцию();
    ВызватьИсключение;
КонецПопытки;
```
Если же в секции `Исключение - КонецПопытки` нет оператора ВызватьИсключение - то тогда и нужно производить запись в ЖР с подробным представлением об ошибке. 

Исключение из этого правила - пример в статье "ИТС: Перехват исключений в коде", пункт 3.1 - когда исключение подавляется на клиенте.

Так же правильно:
```bsl
&НаСервере
Процедура ВыполнитьОперацию()
  Попытка
    // код, приводящий к вызову исключения
    ....
  Исключение
    // Запись события в журнал регистрации для системного администратора.
    ЗаписьЖурналаРегистрации(НСтр("ru = 'Выполнение операции'"),
       УровеньЖурналаРегистрации.Ошибка,,,
       ОбработкаОшибок.ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));
    ВызватьИсключение; // <- ошибка будет перехвачена на клиенте
  КонецПопытки;
КонецПроцедуры

&НаКлиенте
Попытка
    ВыполнитьОперацию();
Исключение
    ТекстСообщения = КраткоеПредставлениеОшибки(ИнформацияОбОшибке());
    ПоказатьПредупреждение(,НСтр("ru = 'Операция не может быть выполнена по причине:'") + Символы.ПС + ТекстСообщения);
КонецПопытки;
```
В этом примере, так как на клиенте не доступен метод "ЗаписьЖурналаРегистрации" - перед вызовом исключения ошибка записывается в ЖР, чтобы потом подавить её и изменить на текст ошибки на понятный пользователю.

## Источники
<!-- Необходимо указывать ссылки на все источники, из которых почерпнута информация для создания диагностики -->
<!-- Примеры источников

* Источник: [Стандарт: Тексты модулей](https://its.1c.ru/db/v8std#content:456:hdoc)
* Полезная информация: [Отказ от использования модальных окон](https://its.1c.ru/db/metod8dev#content:5272:hdoc)
* Источник: [Cognitive complexity, ver. 1.4](https://www.sonarsource.com/docs/CognitiveComplexity.pdf) -->

* [Использование Журнала регистрации](https://its.1c.ru/db/v8std#content:498:hdoc)
* [Перехват исключений в коде](https://its.1c.ru/db/v8std#content:499:hdoc)
