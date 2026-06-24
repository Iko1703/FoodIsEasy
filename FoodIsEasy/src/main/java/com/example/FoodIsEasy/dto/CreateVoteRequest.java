package com.example.FoodIsEasy.dto;

import java.util.List;

public record CreateVoteRequest(String title, List<Long> delishiesIds) {
}
