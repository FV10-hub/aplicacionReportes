package py.com.aplicacion.ws;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public class MetodoFrances {
	public static void main(String[] args) {

		LocalDate fechaVencimiento = LocalDate.of(2023, 12, 5);
		int diaHabilPrimerVencimiento = fechaVencimiento.getDayOfMonth();
		for (int i = 1; i <= 12; i++) {

			if (i == 1) {
				// detalle.setFechaVencimiento(fechaVencimiento);
				// System.out.println("primera cuota::: " + fechaVencimiento);
				System.out.println("fecha primera cuota::: " + i + " valor ::::" + fechaVencimiento);
				continue;
			} else {
				int ultimoDiaHabilActual = YearMonth.from(fechaVencimiento).atEndOfMonth().getDayOfMonth();
				if (ultimoDiaHabilActual < diaHabilPrimerVencimiento) {
					fechaVencimiento = LocalDate.of(fechaVencimiento.getYear(), fechaVencimiento.getMonth(),
							ultimoDiaHabilActual);
					System.out.println("fecha cuota caso 30 31 ::::: " + fechaVencimiento);
					fechaVencimiento = LocalDate.of(fechaVencimiento.getYear(),
							fechaVencimiento.plusMonths(1).getMonth(), diaHabilPrimerVencimiento);
				} else if (fechaVencimiento.getMonth() == Month.FEBRUARY) {
					fechaVencimiento = LocalDate.of(fechaVencimiento.getYear(), fechaVencimiento.getMonth(),
							diaHabilPrimerVencimiento);
					fechaVencimiento = LocalDate.of(fechaVencimiento.getYear(),
							fechaVencimiento.plusMonths(1).getMonth(), diaHabilPrimerVencimiento);
				} else {
					// detalle.setFechaVencimiento(fechaVencimiento);
					fechaVencimiento = fechaVencimiento.plusMonths(1);
				}
				System.out.println("fecha cuota::: " + i + " valor ::::" + fechaVencimiento);
			}

			
		}
	}
}
