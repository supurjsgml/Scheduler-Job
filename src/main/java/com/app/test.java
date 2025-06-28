package com.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class test {
	public static void main(String[] args) {
    	for (int i = 0; i < 5; i++) {
    		List<int[]> pastWinningNumbers = generateRealisticNumbers(10000000);
    		Map<Integer, Integer> frequencyMap = calculateFrequencies(pastWinningNumbers);
    		List<Integer> predictedNumbers = getMarkovBasedNumbers(frequencyMap, pastWinningNumbers);
    		
    		System.out.println("로또 ~~~ : " + predictedNumbers.stream().sorted((e1, e2) -> e1.compareTo(e2)).toList());
		}
    	
    }

	// 1. 현실적인 데이터 기반 생성 (랜덤 X, 빈도수 기반 샘플링)
	private static List<int[]> generateRealisticNumbers(int rounds) {
		Random random = new Random();
		List<int[]> pastWinningNumbers = new ArrayList<>();
		List<Integer> baseNumbers = new ArrayList<>();

		// 1~45까지 숫자의 기본 빈도를 설정
		for (int i = 1; i <= 45; i++) {
			for (int j = 0; j < (random.nextInt(10) + 1); j++) {
				baseNumbers.add(i);
			}
		}

		// 빈도 기반으로 번호를 추출하여 과거 데이터 시뮬레이션
		for (int i = 0; i < rounds; i++) {
			Collections.shuffle(baseNumbers);
			pastWinningNumbers.add(baseNumbers.subList(0, 6).stream().mapToInt(Integer::intValue).toArray());
		}
		

		return pastWinningNumbers;
	}

	// 2. 숫자 빈도수 계산
	private static Map<Integer, Integer> calculateFrequencies(List<int[]> pastWinningNumbers) {
		Map<Integer, Integer> frequencyMap = new HashMap<>();
		for (int[] numbers : pastWinningNumbers) {
			for (int num : numbers) {
				frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
			}
		}
		
		return frequencyMap;
	}

	// 3. 마르코프 체인 방식으로 번호 예측
	private static List<Integer> getMarkovBasedNumbers(Map<Integer, Integer> frequencyMap, List<int[]> pastWinningNumbers) {
		Random random = new Random();
		Map<Integer, Set<Integer>> nextNumberMap = new HashMap<>();

		// 마르코프 체인: 특정 숫자와 함께 나온 숫자들을 기록
		for (int[] numbers : pastWinningNumbers) {
			for (int num : numbers) {
				nextNumberMap.putIfAbsent(num, new HashSet<>());
				for (int other : numbers) {
					if (num != other)
						nextNumberMap.get(num).add(other);
				}
			}
		}

		// 가장 많이 나온 숫자 중 하나를 시작점으로 선정
//		int startNumber = frequencyMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).findFirst().get().getKey();

		Set<Integer> selectedNumbers = new HashSet<>();
		selectedNumbers.add(frequencyMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).findFirst().get().getKey());

		System.out.println(frequencyMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(6).toList());

		// 이후 숫자는 마르코프 체인 확률 기반으로 선택
//		while (selectedNumbers.size() < 6) {
//			List<Integer> possibleNextNumbers = new ArrayList<>(
//					nextNumberMap.getOrDefault(startNumber, new HashSet<>()));
//
//			if (possibleNextNumbers.isEmpty())
//				break;
//
//			startNumber = possibleNextNumbers.get(random.nextInt(possibleNextNumbers.size()));
//			
//			selectedNumbers.add(startNumber);
//		}

//		return selectedNumbers.stream().sorted().collect(Collectors.toList());
		return frequencyMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(6).map(Entry::getKey).toList();
	}
}
