# 🍳 Nimonscooked by Class01-Group-C 

**Nimonscooked** is a chaotic co-op cooking simulation game inspired by *Overcooked!*, built entirely using **Java** and **Swing**.

Control two chefs in a high-pressure kitchen, prepare ingredients, cook dishes, and serve orders before the time runs out!

![Game Menu](src/main/resources/menu_background.png)

## 🎮 Game Description

In Nimonscooked, you play as two chefs (Minions) working together in a kitchen. You must fulfill incoming customer orders by:
1.  **Gathering** raw ingredients from storage.
2.  **Preparing** ingredients (chopping) and **Cooking** them (boiling/frying).
3.  **Plating** the food onto clean plates.
4.  **Serving** the dishes to the customer.
5.  **Washing** dirty plates to keep the cycle going.

Manage your time effectively and ensure you don't burn the food!

## ✨ Key Features

* **Dual Chef Control:** Switch between two chefs (Chef 1 & Chef 2) to multitask efficiently.
* **Dynamic Order System:** Endless orders with increasing pressure. If you fail too many, it's Game Over!
* **Interactive Stations:**
    * 🔪 **Cutting Station:** Chop vegetables and meat.
    * 🔥 **Cooking Station:** Boil pasta or fry ingredients (watch out for burning!).
    * 🍽️ **Plating & Assembly:** Combine ingredients to match recipes.
    * 💧 **Washing Station:** Clean dirty plates returned after serving.
    * 🗑️ **Trash:** Dispose of ruined food.
* **Recipe System:** Create dishes like *Pasta Marinara*, *Bolognese*, and *Frutti di Mare*.

## 🕹️ Controls

| Action | Key | Description |
| :--- | :---: | :--- |
| **Move** | `W`, `A`, `S`, `D` | Move the active chef. |
| **Interact** | `E` | Use stations (Chop, Wash, Start Cooking). |
| **Pick Up / Drop** | `Q` | Pick up item / Drop item / Place on station. |
| **Switch Chef** | `TAB` | Switch control between Chef 1 and Chef 2. |

## 🛠️ Installation & How to Run

### Prerequisites
* Java Development Kit (JDK) 8 or higher.
* An IDE (IntelliJ IDEA, Eclipse, or VS Code) or Terminal.

### Steps
1.  **Clone the repository**
    ```bash
    git clone [https://github.com/username/nimonscooked.git](https://github.com/username/nimonscooked.git)
    cd nimonscooked
    ```

2.  **Compile and Run**
    Navigate to the source directory and run the `Main` class.
    * **Main Class:** `core.Main`

    If using terminal:
    ```bash
    javac -d bin src/main/java/**/*.java
    java -cp bin core.Main
    ```

## 🏗️ Technical Implementation (OOP Concepts)

This project strictly adheres to Object-Oriented Programming principles as per the assignment requirements:

### 1. Inheritance
* `Item` is the parent class for `Ingredient`, `KitchenUtensil`, and `Dish`.
* `Station` is the abstract base class for all specific stations (`CuttingStation`, `CookingStation`, etc.).

### 2. Abstract Class & Interface
* **Abstract Class:** `Station` defines common logic like `progressPercent`, `isInProgress`, and abstract methods for specific station behaviors.
* **Interface:**
    * `Preparable`: Implemented by `Ingredient` (defines `chop()`, `cook()`).
    * `CookingDevice`: Implemented by `BoilingPot` and `FryingPan`.

### 3. Polymorphism
* The game treats different objects as their parent type. For example, the `Chef` holds an `Item`, which could be a `Plate`, `Ingredient`, or `Utensil` at runtime.
* Station interactions are handled polymorphically based on the specific subclass of `Station`.

### 4. Collections
* `List<Chef>`: Manages the chefs in `GameMaster`.
* `Stack<Plate>`: Used in `PlateStorage` to manage clean plates (LIFO).
* `Queue<Order>`: Used in `OrderManager` to manage pending orders (FIFO).
* `Map<Position, TileData>`: Used in `GameMap` to store station data efficiently.

### 5. Concurrency (Multithreading)
* **Game Loop:** Runs on a dedicated thread in `GamePanel`.
* **Station Processes:** Actions like **Cutting**, **Cooking**, and **Washing** run on separate threads with timers, allowing the player to move away while the process completes.

### 6. Design Patterns
* **Singleton Pattern:** Used in `GameMaster` to ensure only one instance of the game controller exists.
* **State Pattern:** Used in `Ingredient` to manage states (`RAW` -> `CHOPPED` -> `COOKED` -> `BURNED`).

## 📂 Project Structure