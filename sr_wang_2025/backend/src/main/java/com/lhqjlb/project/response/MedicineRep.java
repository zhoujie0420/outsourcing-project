package com.lhqjlb.project.response;

import com.lhqjlb.project.entity.Medicine;
import lombok.Data;

import java.util.List;
@Data
public class MedicineRep {
    String medicineCategoryName;
    List<Medicine> list;
}
