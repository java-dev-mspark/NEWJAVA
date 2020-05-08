package com.mspark.newjava.java9.optional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class OptionalImprovements {

	
	public static void main(String[] args) {
		OptionalImprovements oi = new OptionalImprovements();
		oi.optionalStreamImprovement();
	}
	
	private void optionalStreamImprovement() {
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
}
