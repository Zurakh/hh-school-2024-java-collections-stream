package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {


  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    // Есть специальный метод для проверки на пустоту.
    // Исправил ошибку, связанную с попыткой редактирования неизменяемой коллекции
    if (persons.isEmpty()) {
      return Collections.emptyList();
    }

    return persons.stream().skip(1).map(Person::firstName).collect(Collectors.toList());
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    // distinct не нужен, так как всё преобразуется в Set, где и так нет дубликатов
    // И вообще можно короче без потока записать это
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
    // Так короче, лучше читается и сложнее пропустить баг
    return Stream.of(person.secondName(), person.firstName(), person.middleName())
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    // Так будет короче/
    // Прошлый вариант падал, если была послана коллекция,
    // в которой есть персоны совпадающими id
    // Возможно, следовало кидать исключение, чтобы была заметна эта проблема
    return persons
            .stream()
            .collect(Collectors.toMap(
                    Person::id,
                    this::convertPersonToString,
                    (id1, id2) -> id1))                    ;
  }

  // есть ли совпадающие в двух коллекциях персоны?

  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    // Так короче и выполняется не за квадрат, но выделяется память для всех элементов из второй коллекции

    //var secondCollectionSet = new HashSet<>(persons2);
    // Заинлайнил создание сета
    return persons1.stream().anyMatch(new HashSet<>(persons2)::contains);
  }
  // Посчитать число четных чисел

  public long countEven(Stream<Integer> numbers) {
    // Есть специальный метод для подсчёта элементов
    // Прошлый код при одновременном исполнении этого метода будет выдавать ложные результаты,
    // так как несколько потоков будут делать записи в одну глобальную переменную
    return numbers.filter(num -> num % 2 == 0).count();
  }


  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке

  // HashSet основан на HashMap
  //  основан на массиве
  // При добавлении элементов в HashMap, их место в массиве определяется как остаток от деления hashcode на размер массива
  // При создании классов исполняют контракт, согласно которому равные объекты (.equals()) будут иметь равный hashcode, а
  // результат исполнения equals может менять только, если изменилось внутреннее состояние класса, но Integer неизменяемый,
  // поэтому у каждого Integer будет равный hashcode во время исполнения программы, из-за чего каждый Integer будет отображаться
  // в одно и то же место в массиве, если не происходит изменения HashSet'а (удаление элементов, разные capacity)
  // При формировании строки происходит обход массива в одном и том же порядке

  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
  }
}
