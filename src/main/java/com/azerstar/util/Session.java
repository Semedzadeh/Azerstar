package com.azerstar.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Session {
    private static String currentUsername;
    private static int accountId;
    private static int homeBranchId = 1;
    private static Role role = Role.USER;
    private static Set<Section> permissions = EnumSet.noneOf(Section.class);

    // istifadəçinin görə bildiyi filiallar (admin üçün hamısı)
    private static List<Choice> allowedBranches = List.of();
    // yeni qeydlərin yazıldığı filial
    private static int branchId = 1;
    // siyahılarda göstərilən filial: 0 = icazə verilən bütün filiallar
    private static int viewBranchId = 0;

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void login(int account, String username, Role userRole, Set<Section> allowed,
                             List<Choice> branches, int homeBranch) {
        accountId = account;
        currentUsername = username;
        role = userRole;
        permissions = allowed;
        allowedBranches = branches;
        homeBranchId = homeBranch;
        branchId = homeBranch;
        viewBranchId = branches.size() == 1 ? branches.get(0).id() : 0;
        if (branches.size() == 1) branchId = branches.get(0).id();
    }

    public static int getAccountId() {
        return accountId;
    }

    public static Role getRole() {
        return role;
    }

    public static boolean isSuperAdmin() {
        return role == Role.SUPER_ADMIN;
    }

    public static boolean isBranchAdmin() {
        return role == Role.BRANCH_ADMIN;
    }

    /** Baş admin və ya filial admini. */
    public static boolean isAnyAdmin() {
        return role != Role.USER;
    }

    /** Adminlər bütün iş bölmələrini görür (öz filialları daxilində), sadə istifadəçilər yalnız icazə veriləni. */
    public static boolean can(Section section) {
        return isAnyAdmin() || permissions.contains(section);
    }

    /** Baş admin filial əlavə edib-sildikdən sonra siyahını yeniləyir. */
    public static void setAllowedBranches(List<Choice> branches) {
        allowedBranches = branches;
        if (viewBranchId != 0 && !canAccessBranch(viewBranchId)) viewBranchId = 0;
        if (!canAccessBranch(branchId) && !branches.isEmpty()) branchId = branches.get(0).id();
    }

    // ---------- filiallar ----------

    public static List<Choice> getAllowedBranches() {
        return allowedBranches;
    }

    public static boolean hasManyBranches() {
        return allowedBranches.size() > 1;
    }

    public static boolean canAccessBranch(int id) {
        return allowedBranches.stream().anyMatch(b -> b.id() == id);
    }

    /** Giriş ekranından sonra seçilən filial: həm göstərilən, həm də yeni qeydlərin yazıldığı filial olur. */
    public static void chooseBranch(int id) {
        viewBranchId = id;
        branchId = id;
    }

    public static int getBranchId() {
        return branchId;
    }

    public static String getBranchName() {
        return branchName(branchId);
    }

    public static String branchName(int id) {
        for (Choice c : allowedBranches) if (c.id() == id) return c.label();
        return "";
    }

    public static int getViewBranchId() {
        return viewBranchId;
    }

    /** Yuxarı paneldəki filtrdən: 0 = hamısı, əks halda yalnız icazəli filial. */
    public static void setViewBranchId(int id) {
        viewBranchId = id;
        if (id != 0) branchId = id;
    }

    /** İstifadəçinin icazəli olduğu filiallar: "Şəki" və ya "Şəki + Zaqatala". */
    public static String accessLabel() {
        return allowedBranches.stream().map(Choice::label).collect(Collectors.joining(" + "));
    }

    /** Hazırda görünən filialın adı və ya "Hər iki filial". */
    public static String scopeLabel() {
        if (viewBranchId != 0) return branchName(viewBranchId);
        return allowedBranches.size() > 1 ? "Bütün filiallar" : getBranchName();
    }

    /** İstifadəçinin icazəli olduğu bütün filialların nömrələri (süzgəcdən asılı olmayaraq), məsələn "1,2". */
    public static String allowedIds() {
        if (allowedBranches.isEmpty()) return "0";
        return allowedBranches.stream().map(b -> String.valueOf(b.id())).collect(Collectors.joining(","));
    }

    /** SQL üçün: görünən filialların nömrələri, məsələn "1" və ya "1,2". Yalnız tam ədədlərdən qurulur. */
    public static String scopeIds() {
        if (viewBranchId != 0) return String.valueOf(viewBranchId);
        if (allowedBranches.isEmpty()) return "0";
        return allowedBranches.stream().map(b -> String.valueOf(b.id())).collect(Collectors.joining(","));
    }

    /** Siyahılarda: yalnız görünən filialların qeydləri. Qaytarılan SQL parçası " AND sütun IN (…)" formasındadır. */
    public static String branchFilter(String column) {
        return " AND " + column + " IN (" + scopeIds() + ")";
    }

    public static void clearSession() {
        currentUsername = null;
        accountId = 0;
        role = Role.USER;
        permissions = EnumSet.noneOf(Section.class);
        allowedBranches = List.of();
        viewBranchId = 0;
    }
}
