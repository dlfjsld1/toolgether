package com.snackoverflow.toolgether.domain.reservation.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.snackoverflow.toolgether.domain.Post.entity.Post;
import com.snackoverflow.toolgether.domain.Post.repository.PostRepository;
import com.snackoverflow.toolgether.domain.User.entity.User;
import com.snackoverflow.toolgether.domain.User.repository.UserRepository;
import com.snackoverflow.toolgether.domain.reservation.dto.ReservationRequest;
import com.snackoverflow.toolgether.domain.reservation.dto.ReservationResponse;
import com.snackoverflow.toolgether.domain.reservation.entity.Reservation;
import com.snackoverflow.toolgether.domain.reservation.entity.ReservationStatus;
import com.snackoverflow.toolgether.domain.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public ReservationResponse requestReservation(ReservationRequest reservationRequest) {
		Post post = postRepository.findById(reservationRequest.postId())
			.orElseThrow(() -> new RuntimeException("Post not found"));
		User renter = userRepository.findById(reservationRequest.renterId())
			.orElseThrow(() -> new RuntimeException("Renter not found"));
		User owner = userRepository.findById(reservationRequest.ownerId())
			.orElseThrow(() -> new RuntimeException("Owner not found"));

		Double totalAmount = reservationRequest.deposit() + reservationRequest.rentalFee();
		Reservation reservation = Reservation.builder()
			.post(post)
			.renter(renter)
			.owner(owner)
			.startTime(reservationRequest.startTime())
			.endTime(reservationRequest.endTime())
			.amount(totalAmount)
			.status(ReservationStatus.REQUESTED)
			.createAt(LocalDateTime.now())
			.build();

		reservationRepository.save(reservation);
		return new ReservationResponse(reservation.getId(), reservation.getStatus().name(), reservation.getAmount());
	}
}
