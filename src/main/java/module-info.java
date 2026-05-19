module org.example.sudoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;
    requires java.desktop;

    opens org.example.sudoku to javafx.fxml;
    opens org.example.sudoku.controllers to javafx.fxml;
    exports org.example.sudoku;
    exports org.example.sudoku.controllers;
    exports org.example.sudoku.sudoku;
}
