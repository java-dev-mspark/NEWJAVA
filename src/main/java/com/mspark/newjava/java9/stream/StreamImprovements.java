package com.mspark.newjava.java9.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamImprovements {

	public static void main(String[] args) {
		ofNullableImprove();
		takeWhile_dropWhile();
		iterateImprove();
	}
	
	private static void ofNullableImprove() {
		/**
		 * Java8의 경오 Stream factory method of()의 경우 null을 파라미터로 받을 수 없었다. 
		 * 로직을 통해 mapping 신규 스트림을 생성해야 하는 아래와 같은 경우  내부적인 null 체크 로직을 통해  empty stream을 리턴해 주도록 해야 한다.
		 */
		final String configurationDirectory_in_java8_way =
				Stream.of("app.config", "app.home", "user.home")
				.flatMap(key -> {
					final String property = System.getProperty(key);
					return property == null ? Stream.empty() : Stream.of(property);
				})
				.findFirst()
				.orElseThrow(IllegalStateException::new);
		System.out.println(configurationDirectory_in_java8_way);
		
		/**
		 * In JAVA 9
		 * Java9 부터 null 값을 파라미터로 받을 수 있는 factory 메서드 ofNullable() 이 추가 되어 null 파라미터의 경우 empty stream을 리턴하여 코드가 한층 깔끔해 졌다.
		 */
		final String configurationDirectory_in_java9_way =
				Stream.of("app.config", "app.home", "user.home")
				.flatMap(key -> Stream.ofNullable(System.getProperty(key)))
				.findFirst()
				.orElseThrow(IllegalStateException::new);
		
		System.out.println(configurationDirectory_in_java9_way);
	}
	
	/**
	 *  Java9 에서  takeWhile() 메서드와 dropWhile() 메서드가 추가 되었다.
	 */
	private static void takeWhile_dropWhile() {
		
		/**
		 * 특정 숫자 리스트에서 100 이상의 숫자를 가지는 것만 뽑아 리스트를 리턴한다고 가정해 보자
		 * 
		 */
		List<Integer> largeAmountNumberList = IntStream.range(1, 200).boxed().collect(Collectors.toList());
		/**
		 * Java8의 경우 filter를 통해 아래와 같이 간단하게 구현할 수 있었다.
		 * largeAmountNumberList의 크기가 매우 크다고 가정해보자 
		 * Java8의 경우 모든 요소를 탐색하여 필터링을 수행후 종말 연산을 수행한다.
		 */
		List<Integer> over100List_in_java8_way = largeAmountNumberList.stream()
																	  .filter(number -> number >= 100)
																	  .collect(Collectors.toList());
		
		System.out.println(over100List_in_java8_way);
		
		/**
		 * Java9 부터는 정렬된 데이터를 가정 하였을 때
		 * 조건식값을 만족할 때 까지 요소를 취하고 종말연산을 수행하는 takeWhile()
		 * 조건식값을 만족할 때 까지 요소를 버리고 나머지 요소로 종말 연산을 수행하는 dropWhile() 
		 * 
		 * 정렬된 데이터를 기준으로 사용해야한다는 한계는 있지만 매우 큰 데이터를 처리하는데 있어 모든 요소에 대해 필터링 작업을 
		 * 수행하지 않아도 되는것에 장점이 있을것 같다
		 */
		List<Integer> over100List_in_java9_way = largeAmountNumberList.stream()
																	  .dropWhile(number -> number < 100)
																	  .collect(Collectors.toList());
		System.out.println(over100List_in_java9_way);
		
		
	}
	
	private static void iterateImprove() {
		/**
		 * 3이상의 16보다 작은 3의배수의 스트림을 만든다고 가정해보자 
		 * Java8의 경우 아주 간단하게 아래와 같이 작성할 수 있을것이다.
		 * 하지만 실제로 아래의 코드는 우리의 예상대로 동착하지 않는다  +3 을 하는 반복행위가 무한이 반복되어 필터링 조건을 실행하지 못한다.
		 * 
		 * 따라서  range() 통한 범위안에서 필터링을 해야 했었다.
		 */
//		IntStream.iterate(3, x -> x + 3)
//		     	 .filter(x -> x < 16)
//		     	 .forEach(System.out::println);
		IntStream.range(3, 16)
				 .filter(num -> num%3==0)
				 .forEach(System.out::println);
		
		/**
		 * Java9 부터 iterate() 함수의 두번째 파라미터로 반복의 제약조건을 추가 할 수 있어
		 * 단순 iterate() 함수 만으로도 원하는 결과를 얻을 수 있다.
		 */
		IntStream.iterate(3,x -> x < 16,  x -> x + 3)
    	 		 .forEach(System.out::println);
	}
	
}
