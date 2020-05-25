package com.mspark.newjava.java9.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionsImprovements {
	
	public static void main(String[] args) {
		factoryMethodsAdded();
	}
	
	public static void factoryMethodsAdded() {
		
		/**
		 * 문자열 "AAA", "BBB" , "CCC", "DDD" 4개를 가지는 리스트를 초기화 한다고 생각해보자 
		 * 
		 * 일반적 방식으로 객체를 초기화 하고 add() 메스드를 이용하여 요소를 추가하는 아래의 방식은
		 * 명료하지만 너무 장황하다 
		 */
		List<String> listOld = new ArrayList<String>();
		listOld.add("AAA");
		listOld.add("BBB");
		listOld.add("CCC");
		listOld.add("DDD");

		/**
		 * Java 5 이후 부터 아래와 같이  Arrays 객체를 이용하여 리스트 객체를 초기화 할수있도록 개선되었다
		 * 이 방식에는 커다란 오류를 발생시킬 수 있는 위험요소가 존재하는데 아래의 예시를 보자
		 */
		List<String> listJava5 = Arrays.asList("AAA", "BBB", "CCC", "DDD");
	
		try {
			listJava5.add("EEE");
		}
		catch (UnsupportedOperationException e) {
			/**
			 * UnsupportedOperationException 이 발생한다.
			 */
			System.out.println(e);
		}
		
		/**
		 * 얼핀 보면 Arrays.asList()를 통해 반환된 리스트는 immutable 한 것 처럼 보이나 아래의 예를 보면 그렇지 않다는 것을 알 수 있다.
		 */
		String[] hello = { "Hello" };
		List<String> values = Arrays.asList(hello);
		hello[0] = "World";
		System.out.println(values); // >>> World 가 출력 된다.

		/**
		 * Arrays객체를 통한 Collection의 초기화는  List 이외의 다른 컬랙션 객체들을 초기화 하는데는 여전히 장황한 코드를 요구 한다.
		 */
		
		Set<String> set = new HashSet<>(Arrays.asList("AAA", "BBB", "CCC", "DDD"));
		
		
		/**
		 * Java9에서 부터는 다양한 Collection Factory 메서드를 통해 이러한 단점 들을 보완했다.
		 * 아래와 같이 단순한 형태로 다양한 Collection객체를 초기화 할 수 있으며, 이 팩토리 메서드들로 반환된 객체들은 모두 Unmodifiable 하다.
		 */
		
		List<String> listJava9 = List.of("AAA", "BBB", "CCC", "DDD");
		Set<String> setJava9 = Set.of("AAA", "BBB", "CCC", "DDD");
		Map<String, Integer> mapJava9 = Map.of("KEY", 111);
		
		try {
			listJava9.add("test");
		}
		catch (UnsupportedOperationException e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
		
	}
}
