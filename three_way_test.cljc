(ns e5.three-way-test
  (:require [clojure.test :refer [deftest is testing]]
            [statecharts.core :as fsm]
            [e5.three-way :refer [three-way displayed-colours]]))

(defn trace [events]
  (reductions (fn [state event] (fsm/transition three-way state event))
              (fsm/initialize three-way)
              events))

(defn values [events] (mapv :_state (trace events)))
(defn colours [events] (mapv displayed-colours (values events)))

(deftest coordinated-three-way-requirements
  (testing "R1 R7: exactly three red signals at startup"
    (is (= {:w :r :n :r :s :r}
           (first (colours [])))))
  (testing "R6 R9: run does not change colours"
    (is (= (first (colours [])) (second (colours [:run])))))
  (testing "R4 R8: next moves first through west, north, south, repeat"
    (let [v (values (into [:run] (repeat 16 :next)))]
      (is (= [:w :g] (nth v 3)))
      (is (= [:n :g] (nth v 7)))
      (is (= [:s :g] (nth v 11)))
      (is (= [:w :g] (nth v 15)))))
  (testing "R2 R5: no overlapping green/amber signals during long run"
    (doseq [lamps (colours (into [:run] (repeat 96 :next)))]
      (is (<= (count (filter #{:g :a :ra} (vals lamps))) 1))))
  (testing "R3: exactly four next events per approach (round robin)"
    (is (= [:n :r] (last (values (into [:run] (repeat 4 :next))))))
    (is (= [:s :r] (last (values (into [:run] (repeat 8 :next)))))))
  (testing "R10: halt during green remains on same colour until next"
    (is (= [[:w :g] [:stopping-w :g] [:stopping-w :a] :stopped]
           (subvec (values [:run :next :next :halt :next :next]) 3)))
    (is (= {:w :r :n :r :s :r}
           (last (colours [:run :next :next :halt :next :next]))))))

(deftest additional-safety-and-liveness-checks
  (testing "R4 R5 R6: each next event changes at most one visible signal"
    (let [observed (colours (into [:run] (repeat 120 :next)))]
      (doseq [[before after] (partition 2 1 observed)]
        (is (<= (count (filter (fn [k] (not= (get before k) (get after k)))
                                [:w :n :s])) 1)))))
  (testing "R8: all three approaches receive equal green opportunities"
    (let [observed (colours (into [:run] (repeat 120 :next)))
          green-count (fn [approach]
                        (count (filter #(= :g (get % approach)) observed)))]
      (is (= (green-count :w) (green-count :n) (green-count :s)))))
  (testing "R10: halt from North or South green completes a legal stopping cycle"
    (doseq [[steps signal] [[6 :n] [10 :s]]]
      (let [prefix (into [:run] (repeat steps :next))
            before-halt (last (colours prefix))
            after-halt (last (colours (conj prefix :halt)))
            at-rest (last (colours (into (conj prefix :halt)
                                             (repeat 2 :next))))]
        (is (= :g (get before-halt signal)))
        (is (= before-halt after-halt))
        (is (= {:w :r :n :r :s :r} at-rest))))))
