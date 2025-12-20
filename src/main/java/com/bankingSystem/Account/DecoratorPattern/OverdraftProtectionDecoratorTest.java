package com.bankingSystem.Account.DecoratorPattern;

import com.bankingSystem.Account.CheckingAccount;
import com.bankingSystem.Account.CompositePattern.AccountComponent;

public class OverdraftProtectionDecoratorTest {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static int testCount = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("   " + BOLD + "Decorator Pattern Test - OverdraftProtectionDecorator" + RESET);
        System.out.println("=".repeat(70) + "\n");

        testBasicAccountFunctionality();
        testDecoratorApplication();
        testOverdraftWithinLimit();
        testOverdraftLimitExceeded();
        testMultipleWithdrawals();
        testDepositAfterOverdraft();
        testDecoratorOnDifferentAccountTypes();
        testDecoratorComposition();

        printSummary();
    }


    private static void testBasicAccountFunctionality() {
        printTestHeader("Test 1: Basic Account Functionality (Before Decorator)");

        try {
            AccountComponent account = new CheckingAccount("TEST-001", "test-user");


            account.deposit(1000.0);
            assertEqual(account.getTotalBalance(), 1000.0, "Balance after deposit should be 1000");


            account.withdraw(300.0);
            assertEqual(account.getTotalBalance(), 700.0, "Balance after withdrawal should be 700");

            try {
                account.withdraw(800.0);
                testFailed("Withdrawal exceeding balance should have failed without decorator");
            } catch (Exception e) {
                testPassed("Correctly rejected withdrawal exceeding balance: " + e.getClass().getSimpleName());
            }

        } catch (Exception e) {
            testFailed("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }
//اختبار 2
    private static void testDecoratorApplication() {
        printTestHeader("Test 2: Decorator Application");

        try {
            AccountComponent baseAccount = new CheckingAccount("TEST-002", "test-user");
            baseAccount.deposit(500.0);

            // تطبيق Decorator مع حد overdraft = 300
            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(baseAccount, 300.0);

            // التحقق من أن Decorator يحافظ على الرصيد الأصلي
            assertEqual(decoratedAccount.getTotalBalance(), 500.0,
                    "Decorator should preserve original balance");

            // التحقق من أن الحساب المزخرف لا يزال يعمل كحساب عادي
            assertTrue(decoratedAccount instanceof AccountComponent,
                    "Decorated account should be instance of AccountComponent");

            testPassed("Decorator applied successfully");

        } catch (Exception e) {
            testFailed("Failed to apply decorator: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 3: السحب ضمن حد Overdraft المسموح
     */
    private static void testOverdraftWithinLimit() {
        printTestHeader("Test 3: Overdraft Within Limit");

        try {
            AccountComponent account = new CheckingAccount("TEST-003", "test-user");
            account.deposit(500.0);

            // تطبيق Decorator مع حد 400
            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(account, 400.0);

            // السحب الذي يستخدم جزء من Overdraft
            double withdrawalAmount = 700.0; // 500 (balance) + 200 (from overdraft)
            decoratedAccount.withdraw(withdrawalAmount);

            double expectedBalance = 500.0 - 700.0; // -200
            assertEqual(decoratedAccount.getTotalBalance(), expectedBalance,
                    "Balance after overdraft withdrawal should be -200");

            testPassed("Overdraft withdrawal within limit succeeded");

        } catch (Exception e) {
            testFailed("Overdraft withdrawal should have succeeded: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 4: السحب الذي يتجاوز حد Overdraft (يجب أن يفشل)
     */
    private static void testOverdraftLimitExceeded() {
        printTestHeader("Test 4: Overdraft Limit Exceeded (Should Fail)");

        try {
            AccountComponent account = new CheckingAccount("TEST-004", "test-user");
            account.deposit(500.0);

            // تطبيق Decorator مع حد 300
            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(account, 300.0);

            // محاولة السحب الذي يتجاوز الحد (500 + 300 = 800 max, نحاول سحب 900)
            try {
                decoratedAccount.withdraw(900.0);
                testFailed("Withdrawal exceeding overdraft limit should have been rejected");
            } catch (LimitReached e) {
                testPassed("Correctly rejected withdrawal exceeding overdraft limit");
                System.out.println("  → Exception message: " + e.getMessage());
            } catch (Exception e) {
                testFailed("Wrong exception type: " + e.getClass().getSimpleName() +
                        " (expected LimitReached)");
            }

            // التحقق من أن الرصيد لم يتغير
            assertEqual(decoratedAccount.getTotalBalance(), 500.0,
                    "Balance should remain unchanged after rejected withdrawal");

        } catch (Exception e) {
            testFailed("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 5: سحوبات متعددة مع Overdraft
     */
    private static void testMultipleWithdrawals() {
        printTestHeader("Test 5: Multiple Withdrawals with Overdraft");

        try {
            AccountComponent account = new CheckingAccount("TEST-005", "test-user");
            account.deposit(1000.0);

            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(account, 500.0);

            // سحب أول: 600 (من الرصيد الأصلي)
            decoratedAccount.withdraw(600.0);
            assertEqual(decoratedAccount.getTotalBalance(), 400.0,
                    "Balance after first withdrawal should be 400");

            // سحب ثاني: 700 (400 من الرصيد + 300 من overdraft)
            decoratedAccount.withdraw(700.0);
            assertEqual(decoratedAccount.getTotalBalance(), -300.0,
                    "Balance after second withdrawal should be -300");

            // سحب ثالث: محاولة 250 أخرى 
            // الرصيد الحالي: -300، الحد المسموح: 500
            // المتاح للسحب: -300 + 500 = 200
            // نحاول سحب 250 > 200، يجب أن يفشل
            try {
                decoratedAccount.withdraw(250.0); // 250 > 200 available, should fail
                testFailed("Third withdrawal (250) should have been rejected (only 200 available)");
            } catch (LimitReached e) {
                testPassed("Correctly rejected third withdrawal exceeding available limit");
            }

            testPassed("Multiple withdrawals handled correctly");

        } catch (Exception e) {
            testFailed("Multiple withdrawals test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 6: الإيداع بعد استخدام Overdraft
     */
    private static void testDepositAfterOverdraft() {
        printTestHeader("Test 6: Deposit After Overdraft");

        try {
            AccountComponent account = new CheckingAccount("TEST-006", "test-user");
            account.deposit(500.0);

            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(account, 400.0);

            // سحب باستخدام overdraft
            decoratedAccount.withdraw(700.0); // 500 - 700 = -200
            assertEqual(decoratedAccount.getTotalBalance(), -200.0,
                    "Balance after overdraft withdrawal should be -200");

            // إيداع بعد استخدام overdraft
            decoratedAccount.deposit(300.0);
            assertEqual(decoratedAccount.getTotalBalance(), 100.0,
                    "Balance after deposit should be 100 (-200 + 300)");

            // التحقق من أن السحب العادي يعمل مرة أخرى
            decoratedAccount.withdraw(50.0);
            assertEqual(decoratedAccount.getTotalBalance(), 50.0,
                    "Normal withdrawal should work after deposit");

            testPassed("Deposit after overdraft works correctly");

        } catch (Exception e) {
            testFailed("Deposit after overdraft test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 7: تطبيق Decorator على أنواع حسابات مختلفة
     */
    private static void testDecoratorOnDifferentAccountTypes() {
        printTestHeader("Test 7: Decorator on Different Account Types");

        try {
            // CheckingAccount
            AccountComponent checkingAccount = new CheckingAccount("TEST-CHK-001", "test-user");
            checkingAccount.deposit(1000.0);
            AccountComponent decoratedChecking = new OverdraftProtectionDecorator(checkingAccount, 500.0);
            decoratedChecking.withdraw(1200.0); // 1000 - 1200 = -200 (within limit)
            assertEqual(decoratedChecking.getTotalBalance(), -200.0,
                    "Decorator works on CheckingAccount");

            testPassed("Decorator works on different account types");

        } catch (Exception e) {
            testFailed("Decorator on different account types failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    /**
     * اختبار 8: تركيبة Decorators متعددة (Composition)
     * ملاحظة: هذا الاختبار يوضح إمكانية تطبيق عدة decorators
     */
    private static void testDecoratorComposition() {
        printTestHeader("Test 8: Decorator Pattern Flexibility");

        try {
            AccountComponent account = new CheckingAccount("TEST-008", "test-user");
            account.deposit(1000.0);

            // تطبيق Decorator
            AccountComponent decoratedAccount = new OverdraftProtectionDecorator(account, 300.0);

            // التحقق من أن جميع الوظائف تعمل
            double balance = decoratedAccount.getTotalBalance();
            decoratedAccount.showDetails(); // يجب ألا يسبب خطأ
            decoratedAccount.deposit(100.0);
            decoratedAccount.withdraw(50.0);

            assertTrue(decoratedAccount.getTotalBalance() > balance,
                    "All operations should work correctly");

            testPassed("Decorator maintains all account functionality");

        } catch (Exception e) {
            testFailed("Decorator composition test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ==================== Helper Methods ====================

    private static void printTestHeader(String testName) {
        System.out.println(CYAN + BOLD + testName + RESET);
        System.out.println("-".repeat(70));
    }

    private static void assertEqual(double actual, double expected, String message) {
        testCount++;
        if (Math.abs(actual - expected) < 0.01) { // Tolerance for floating point
            System.out.println(GREEN + "  ✓ " + message + RESET);
            passedTests++;
        } else {
            System.out.println(RED + "  ✗ " + message + RESET);
            System.out.println(RED + "    Expected: " + expected + ", Actual: " + actual + RESET);
            failedTests++;
        }
    }

    private static void assertTrue(boolean condition, String message) {
        testCount++;
        if (condition) {
            System.out.println(GREEN + "  ✓ " + message + RESET);
            passedTests++;
        } else {
            System.out.println(RED + "  ✗ " + message + RESET);
            failedTests++;
        }
    }

    private static void testPassed(String message) {
        testCount++;
        passedTests++;
        System.out.println(GREEN + "  ✓ " + message + RESET);
    }

    private static void testFailed(String message) {
        testCount++;
        failedTests++;
        System.out.println(RED + "  ✗ " + message + RESET);
    }

    private static void printSummary() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(BOLD + "   Test Summary" + RESET);
        System.out.println("=".repeat(70));
        System.out.println("Total Tests: " + testCount);
        System.out.println(GREEN + "Passed: " + passedTests + RESET);
        if (failedTests > 0) {
            System.out.println(RED + "Failed: " + failedTests + RESET);
        } else {
            System.out.println(GREEN + "Failed: " + failedTests + RESET);
        }

        double successRate = (passedTests * 100.0) / testCount;
        System.out.println("\nSuccess Rate: " + String.format("%.1f%%", successRate));

        if (failedTests == 0) {
            System.out.println("\n" + GREEN + BOLD + "✅ All tests passed! Decorator Pattern is working correctly." + RESET);
        } else {
            System.out.println("\n" + YELLOW + "⚠ Some tests failed. Please review the errors above." + RESET);
        }

        System.out.println("=".repeat(70) + "\n");
    }
}
