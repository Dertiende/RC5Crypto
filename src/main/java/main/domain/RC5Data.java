package main.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rc5")
public class RC5Data {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rc5_id_seq")
	@SequenceGenerator(name = "rc5_id_seq", allocationSize = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "key")
	private String key;

	@Column(name = "rounds")
	private Integer rounds = 2;

	@Column(name = "block_size")
	private Integer bsize = 16;

	@Column(name = "vector")
	private Long vector;

	@Column(name = "hash")
	private Long hash;

	@Column(name = "size")
	private Long size;

}