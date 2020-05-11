package com.mspark.newjava.java9.optional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class OptionalImprovements {

	
	public static void main(String[] args) {
		OptionalImprovements oi = new OptionalImprovements();
		oi.optionalStreamAdded();
		oi.ifPresentOrElseAdded();
		oi.orAdded();
		
	}
	
	private void optionalStreamAdded() {
		List<String> names = List.of("1", "2", "3", "5");
		
		/**
		 * Java8의 경우 Nullable 한 Optional 객체를 리턴할 때는 filter()의 단계를 거쳐
		 * not null 데이터를 수집하는 형식으로 작성해야 했다.
		 */
		List<String> lookedUpNames_in_java8_way =
				names.stream()
					 .map(this::lookUpName)
					 .filter(Optional::isPresent)
					 .map(Optional::get)
					 .collect(toList());

		System.out.println(lookedUpNames_in_java8_way);
		
		/**
		 * Java9의 경우 Optional 객체에 stream() 함수가 추가되어 요소가 존재하는 경우 요소로 이루어진 Stream을 리턴하고 
		 * 값이 없는겨우 빈 Stream을 리턴한다.
		 * 
		 * 여러 스트림을 하나의 스레드로 합치는 Stream.flatMap() 메서드와 함께 사용하여 코드를 조금 더 간결하게 작성할 수 있다.
		 */
		List<String> lookedUpNames_in_java9_way =
				names.stream()
				     .map(this::lookUpName)
					 .flatMap(Optional::stream)
				     .collect(toList());
		
		System.out.println(lookedUpNames_in_java9_way);
		
	}
	
	/**
	 * @param name
	 * @return
	 */
	private Optional<String> lookUpName(String name){
		List<String> names = List.of("1","5");
		if(names.contains(name)) {
			return Optional.of(name);
		}else {
			return Optional.empty();
		}
	}
	
	
	private void ifPresentOrElseAdded() {
		String name = "1";
		Optional<String> lookedUpName = lookUpName(name);

		/**
		 * 조회한 이름이 존재하면 success를 출력하는 메서드를 호출하고 존재하지 않으면 fail을 출력하는 메서드를 출력.
		 */
		
		/**
		 * Java8의 경우  Optional.isPresent() 메서드를 통해 분기를 태워 처리 할 수 있었다.
		 */
		if(lookedUpName.isPresent()) {
			printSuccess(lookedUpName.get());
		}else {
			printFail();
		}
		
		/**
		 * Java8의 경우 Optional.ifPresent() 메서드를 통한 함수형 프로그래밍 기법으로 넘겨받은 callback 함수를 실행 시킬 수 있다.
		 * 하지만 존재하지 않는경우에 대한 처리는 존재 하지 않았다.
		 */
		lookedUpName.ifPresent(this::printSuccess);
		
		/**
		 * Java9 에서  Optional.ifPresentOrElse() 메서드가 추가되면서, 값이 존재할때와 존재하지 않을 때의 액션을 동시에 처리 할 수 있게 되었다.
		 */
		lookedUpName.ifPresentOrElse(this::printSuccess, this::printFail);
		
	}
	
	private void printSuccess(String name) {
		System.out.println(name + "success");
	}
	
	private void printFail() {
		System.out.println("fail");
	}
	
	private void orAdded() {
		Optional<String> nameOptional = lookUpName("aa");
		
		/**
		 * lookUpName() 을통해 조회한 데이터가 존재하지 않으면 lookUpName2()를 통해 이름을 조회하는 로직이 있다고 가정하자.
		 * Java8의 경우 Optional.orElseGet() 메서드를 통해 값이 잇으면 반환하고 값이 없을경우 넘겨받은 함수를 실행하여 값을 반환할 수 있다.
		 * 
		 * 아래와 같은 방법은 lookUpName2() 메서드가 NULL을 반환할 가능성이 존재하며 다시 한 번 확인을 위해 if 문과 같은 소스가 추가되어 
		 * 소스가 더러워질 수 밖에 없다.
		 * 
		 */
		String lookedUpName = nameOptional.orElseGet(() -> lookUpName2("aa"));
		
		if(lookedUpName != null) {
			// Do Somthing
		}
		
		/**
		 * Java9 에서 or() 메서드가 추가되면서 특정 타입의 객체가 아닌 새로운 Optional을 리턴할 수 있게 됬다.
		 * 새로운 Optional을 리턴받음으로써  Null 값에 대한 처리를 Optional 객체를 통해 진행하여 코드가 더 깔금해지고 명확해질 수 있다
		 */
		Optional<String> lookedUpNameOptional = nameOptional.or(() -> lookUpName3("aa"));
		
		lookedUpNameOptional.ifPresent(System.out::println);
	}
	
	private String lookUpName2(String name){
		return null;
	}
	
	private Optional<String> lookUpName3(String name){
		List<String> names = List.of("1","5");
		if(names.contains(name)) {
			return Optional.of(name);
		}else {
			return Optional.empty();
		}
	}
}
