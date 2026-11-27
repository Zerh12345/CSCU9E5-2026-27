(ns e5.one-way-test
  (:require [clojure.test :refer [deftest is testing]]
            [statecharts.core :as fsm]
            [e5.one-way :refer [one-way]]))

(defn trace [events]
  (reductions (fn [state event] (fsm/transition one-way state event))
              (fsm/initialize one-way)
              events))

(defn values [events] (mapv :_state (trace events)))

(defn colour [value]
  (if (vector? value) (last value) :r))

(deftest one-way-requirements
  (testing "R7 R9: all red initially, run arms without changing lamp colour"
    (is (= [:stopped [:running :r]] (values [:run]))))
  (testing "R4 R6 R8: legal cycle follows :next and reaches green"
    (is (= [:r :r :ra :g :a :r]
           (mapv colour (values [:run :next :next :next :next])))))
  (testing "R10: halt from green leaves lamp green until next event"
    (is (= [[:running :g] [:stopping :g] [:stopping :a] :stopped]
           (subvec (values [:run :next :next :halt :next :next]) 3))))
  (testing "R10: halt at red stops immediately without colour change"
    (is (= :stopped (:_state (last (trace [:run :halt]))))))
  (testing "stopped controller ignores :next and resumes on :run"
    (is (= [:stopped :stopped [:running :r]]
           (values [:next :run])))))

(deftest additional-controller-checks
  (testing "R6 R10: halt while red+amber does not change displayed colour"
    (is (= :ra (colour (last (values [:run :next :halt])))))
    (is (= :stopped (last (values [:run :next :halt
                                  :next :next :next])))))
  (testing "R6: repeated :run during active cycle does not change lamps"
    (is (= :g (colour (last (values [:run :next :next :run])))))))
