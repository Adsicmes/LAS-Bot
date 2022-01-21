package commmand;

class GearStrategyOne implements Strategy {
    @Override
    public void exec(String param) {
        System.out.println("当前档位" + param);
    }
}
