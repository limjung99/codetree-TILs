import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


class Query{
    int op;
    int t;
    int x;
    String name;
    int n;

    public Query(){}

    public Query(int op, int t, int x, int n, String name) {
        this.op = op;
        this.t = t;
        this.x = x;
        this.n = n;
        this.name = name;
    }
}

public class Main {
    static int L,Q;
    static HashSet<String> customerSet = new HashSet<>();
    static HashMap<String,Integer> customerTimeMap = new HashMap<>();
    static HashMap<String,Integer> customerPosMap = new HashMap<>();
    static HashMap<String,Integer> customerEatCntMap = new HashMap<>();
    static StringBuilder sb = new StringBuilder();
    static List<Query> queries = new ArrayList<>();

    static void debug(Query q){
        System.out.println("------------------");
        System.out.println("op : "+q.op);
        System.out.println("t : "+q.t);
        System.out.println("x : "+q.x);
        System.out.println("n : "+q.n);
        System.out.println("name : "+q.name);
    }

    // 쿼리를 받아서 저장
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        for(int i=0;i<Q;i++){
            int op=-1, t=-1, x=-1,n = -1;
            String name = "";
            st = new StringTokenizer(br.readLine());
            op = Integer.parseInt(st.nextToken());
            t = Integer.parseInt(st.nextToken());

            if(op == 100){
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
            } else if(op == 200){
                x = Integer.parseInt(st.nextToken());
                name = st.nextToken();
                n = Integer.parseInt(st.nextToken());

                // memory when customer in HashMap
                customerSet.add(name);
                customerTimeMap.put(name,t);
                customerPosMap.put(name,x);
                customerEatCntMap.put(name,n);
            }

            Query q = new Query(op,t,x,n,name);
            queries.add(q);
        }

        solve();

        System.out.println(sb);
    }

    private static void solve() {
        // op == 100 인 경우에 대해서 언제 손님에게 도착하는지 계산
        List<Query> sushiEatList = new ArrayList<>();

        for(Query q : queries){
            if(q.op != 100){
                continue;
            }

            Query sushi = q;

            // 초밥이 만들어지는 경우 언제 손님과 만나게 되는지 미리 계산
            int expectedTimeForMatch = 0; // 초밥과 손님이 만날때까지의 예상 소요 시간
            int expectedMatchTime  = 0; // 초밥과 손님이 만나는 시간

            if(sushi.t < customerTimeMap.get(sushi.name)){
                // 초밥 생성 시점이 customer 입장 미만일 경우

                int diff = customerTimeMap.get(sushi.name) - sushi.t; // 손님 입장과 초밥 생성의 시간 차이
                int sushiPosWhenCustomerIn = (sushi.x + diff)%L; // 손님 입장시 초밥의 현재 포지션

                if(customerPosMap.get(sushi.name) < sushiPosWhenCustomerIn){
                    // 초밥이 손님 기준 시계방향에 위치
                    expectedTimeForMatch = L - sushiPosWhenCustomerIn + customerPosMap.get(sushi.name);
                }
                else {
                    // 초밥이 손님 기준 반시계방향에 위치
                    expectedTimeForMatch = customerPosMap.get(sushi.name) - sushiPosWhenCustomerIn;
                }

                // matchTime = customer in time + expected match time
                expectedMatchTime = customerTimeMap.get(sushi.name) + expectedTimeForMatch;
            } else {
                // customer 입장 후 초밥이 만들어짐

                if(customerPosMap.get(sushi.name) < sushi.x){
                    expectedTimeForMatch = L - sushi.x + customerPosMap.get(sushi.name);
                } else {
                    expectedTimeForMatch = customerPosMap.get(sushi.name) - sushi.x;
                }

                // matchTime = sushi in time + expected match time
                expectedMatchTime = sushi.t + expectedTimeForMatch;
            }

            // 사라지는 명령 생성
            Query sushiQuery = new Query();
            sushiQuery.op = q.op + 1;
            sushiQuery.t = expectedMatchTime;
            sushiQuery.name = q.name;
            sushiEatList.add(sushiQuery);
        }

        queries.addAll(sushiEatList);

        // (1) t (2) op 에 대해서 ordered sort
        Collections.sort(queries,(a,b)->{
            if(a.t == b.t){
                return a.op - b.op;
            }
            return a.t - b.t;
        });

        int sushiCount = 0;
        int customerCount = 0;
        // 순서대로 카운트
        for(int i=0;i<queries.size();i++){
            Query q = queries.get(i);

            if(q.op == 100){
                sushiCount++;
            } else if(q.op == 101){
                sushiCount--;
                int cnt = customerEatCntMap.get(q.name);
                cnt--;
                if(cnt == 0){
                    customerCount--;
                }
                customerEatCntMap.put(q.name,cnt);
            } else if(q.op == 200){
                customerCount++;
            } else if(q.op == 300){
                sb.append(customerCount).append(" ").append(sushiCount).append("\n");
            }
        }
    }
}