package com.mspark.newjava.java9.collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class CollectorsImprovements {

	public static void main(String[] args) {
		filteringCollectors();
	}
	
	private static void filteringCollectors() {
		List<Expense> expenses = new ArrayList<Expense>();
		
		/**
		 * Amount 1000 이상의 Expense 리스트를 연도별로 분리
		 * 
		 * Java8의 경우 필터링 후 Collectors.groupingBy를 통해 종말연산을 수행
		 */
		Map<Integer, List<Expense>> listByYearMore1000_in_java8_way
			= expenses.stream()
					  .filter(expense -> expense.getAmount() > 1_000)
					  .collect(groupingBy(Expense::getYear));
		
		/**
		 * Java9의 경우 종말 연산에 함께 Collectors 제공 정적함수를 통해 필터링을 수행 할 수 있다.
		 */
		Map<Integer, List<Expense>> listByYearMore1000_in_java9_way
		= expenses.stream()
				  .collect(groupingBy(Expense::getYear, 
						  filtering(expense -> expense.getAmount() >1_000, toList())));
	}
	
	
	private void flatMappingImprovement() {
		List<Expense> expenses = new ArrayList<Expense>();
		
		/**
		 * 연도별 Tag의 셋을 도출한다고 가정해 보자 
		 */
		
		/**
		 * Java8 에서 제공하는 Collecotrs.mapping() 메서드로는 Map<Integer, Set<List<Tag>>> 형태의 데이터 밖에 리턴하지 못한다.
		 */
		expenses.stream()
	    		.collect(groupingBy(Expense::getYear,
	    						mapping(Expense::getTags, toSet())
	    				));
		
		/**
		 * Java9 에서는 Collectors 클래스의 flatMapping() 이라는 함수를 제공한다
		 * flatMapping() 은 각 요소드의 스트림을 하나의 컨테이너로 변경 할 수 있다.
		 * 스트림의 여러 요소들을 하나의 인풋스트림으로 합쳐주는 Stream.flatMap() 메서드와 비슷하다.
		 */
		Map<Integer, Set<Tag>> tagsByYear = expenses
			    .stream()
			    .collect(groupingBy(Expense::getYear,
			        flatMapping(expense -> expense.getTags().stream(), toSet())
			    ));
		
	}
	
}

class Expense {
    private final long amount;
    private final int year;
    private final List<Tag> tags;

    Expense(long amount, int year, List<Tag> tags) {
        this.amount = amount;
        this.year = year;
        this.tags = tags;
    }

    long getAmount() {
        return amount;
    }

    int getYear() {
        return year;
    }

    List<Tag> getTags() {
        return tags;
    }
}

enum Tag {
    FOOD, ENTERTAINMENT, TRAVEL, UTILITY
}