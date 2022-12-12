package com.foober.foober.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity(name = "Admin")
@Table(name = "admins")
public class Admin extends User {
}